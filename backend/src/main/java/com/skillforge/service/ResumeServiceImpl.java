package com.skillforge.service;

import com.skillforge.dto.AtsAnalysisResult;
import com.skillforge.dto.PageResponse;
import com.skillforge.dto.ResumeFileDownload;
import com.skillforge.dto.ResumeResponse;
import com.skillforge.dto.ResumeSummaryResponse;
import com.skillforge.event.ResumeUploadedEvent;
import com.skillforge.exception.BadRequestException;
import com.skillforge.exception.FileStorageException;
import com.skillforge.exception.MaliciousFileException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.Resume;
import com.skillforge.model.Skill;
import com.skillforge.model.User;
import com.skillforge.repository.ApplicationRepository;
import com.skillforge.repository.ResumeRepository;
import com.skillforge.repository.SkillRepository;
import com.skillforge.repository.UserRepository;
import com.skillforge.util.FileSignatureValidator;
import com.skillforge.util.PageRequestFactory;
import com.skillforge.util.ResumeTextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String DOCX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ResumeTextExtractor resumeTextExtractor;
    private final FileStorageService fileStorageService;
    private final SkillExtractionService skillExtractionService;
    private final AtsScoringService atsScoringService;
    private final ApplicationRepository applicationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final FileSignatureValidator fileSignatureValidator;
    private final VirusScanner virusScanner;

    @Override
    @Transactional
    public ResumeResponse upload(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please select a file to upload");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        String fileType = resumeTextExtractor.resolveFileType(file.getOriginalFilename());
        fileSignatureValidator.validateDeclaredContentType(fileType, file.getContentType());

        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException ex) {
            throw new FileStorageException("Failed to read uploaded file", ex);
        }
        fileSignatureValidator.validateMagicBytes(content, fileType);

        VirusScanner.ScanResult scanResult = virusScanner.scan(content, file.getOriginalFilename());
        if (!scanResult.clean()) {
            throw new MaliciousFileException("Uploaded file failed the security scan: " + scanResult.detail());
        }

        // Both extractText and store used to re-read the MultipartFile from scratch
        // (each via its own getBytes()/getInputStream() call) - for a file backed by
        // a temp-file-on-disk multipart implementation that's 2 extra full disk reads
        // on top of the one above. Passing the bytes we already have through instead
        // caps this upload at a single read of the request body, at the cost of
        // holding one 5MB-max buffer in memory for the duration of the request -
        // true zero-copy streaming isn't possible here regardless, since PDFBox/POI
        // both need random access to the whole document to parse its structure.
        String text = resumeTextExtractor.extractText(content, fileType);
        String storedFileName = fileStorageService.store(userId, content, fileType);

        List<Skill> catalog = skillRepository.findAll();
        Set<Skill> extractedSkills = skillExtractionService.extractSkills(text, catalog);
        AtsAnalysisResult analysis = atsScoringService.analyze(text, extractedSkills);

        Resume resume = Resume.builder()
                .user(user)
                .title(file.getOriginalFilename())
                .content(text)
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .fileType(fileType)
                .fileSizeBytes(file.getSize())
                .atsScore(analysis.getScore())
                .skills(extractedSkills)
                .build();

        resumeRepository.save(resume);
        // Module 2 hook: the deep ATS Intelligence report is built asynchronously
        // (after this transaction commits) by AtsAnalysisEventListener, so a bug
        // in that engine can never fail or roll back a resume upload.
        eventPublisher.publishEvent(new ResumeUploadedEvent(resume.getId()));

        return toResponse(resume, analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getLatestForUser(Long userId) {
        Resume resume = resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No resume found for this user"));

        AtsAnalysisResult analysis = atsScoringService.analyze(resume.getContent(), resume.getSkills());
        return toResponse(resume, analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getById(Long resumeId) {
        Resume resume = requireResume(resumeId);
        AtsAnalysisResult analysis = atsScoringService.analyze(resume.getContent(), resume.getSkills());
        return toResponse(resume, analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ResumeSummaryResponse> listHistory(Long userId, Integer page, Integer size, String sort) {
        // "Current" always means "most recently uploaded", independent of whatever
        // page/sort the caller asked for - resolved once up front rather than
        // inferred from position 0 of a (possibly re-sorted, possibly page-2) result.
        Long currentId = resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId)
                .map(Resume::getId)
                .orElse(null);

        Pageable pageable = PageRequestFactory.of(page, size, resolveResumeSort(sort));
        Page<Resume> history = resumeRepository.findByUserIdAndDeletedFalse(userId, pageable);

        return PageResponse.from(history.map(resume -> ResumeSummaryResponse.builder()
                .id(resume.getId())
                .originalFileName(resume.getOriginalFileName())
                .fileType(resume.getFileType())
                .fileSizeBytes(resume.getFileSizeBytes())
                .atsScore(resume.getAtsScore())
                .current(resume.getId().equals(currentId))
                .uploadedAt(resume.getCreatedAt())
                .build()));
    }

    private Sort resolveResumeSort(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortKey) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "highestScore" -> Sort.by(Sort.Direction.DESC, "atsScore");
            default -> throw new BadRequestException(
                    "Invalid sort key: " + sortKey + ". Valid values: newest, oldest, highestScore");
        };
    }

    @Override
    @Transactional
    public void deleteResume(Long userId, Long resumeId) {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found: " + resumeId));

        if (resume.isDeleted()) {
            throw new ResourceNotFoundException("Resume not found: " + resumeId);
        }

        resume.setDeleted(true);
        resumeRepository.save(resume);

        // Only reclaim disk space if no application still references this exact resume -
        // a recruiter reviewing a past application must still be able to open the file
        // the candidate actually applied with, even after the candidate deletes it.
        if (!applicationRepository.existsByResumeId(resumeId)) {
            fileStorageService.delete(userId, resume.getStoredFileName());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeFileDownload loadFile(Long resumeId) {
        Resume resume = requireResume(resumeId);
        var resource = fileStorageService.load(resume.getUser().getId(), resume.getStoredFileName());
        String contentType = resume.getFileType().equalsIgnoreCase("PDF") ? PDF_CONTENT_TYPE : DOCX_CONTENT_TYPE;
        return new ResumeFileDownload(resource, resume.getOriginalFileName(), contentType, resume.getUser().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public void assertOwner(Long resumeId, Long userId) {
        Resume resume = requireResume(resumeId);
        if (!resume.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only perform this action on your own resume");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void assertViewable(Long resumeId, Long requesterId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }

        Resume resume = requireResume(resumeId);
        if (resume.getUser().getId().equals(requesterId)) {
            return;
        }
        if (applicationRepository.existsByResume_IdAndJob_PostedById(resumeId, requesterId)) {
            return;
        }

        throw new AccessDeniedException("You do not have permission to access this resume");
    }

    /**
     * Direct by-id lookup used by getById/loadFile. Deliberately does NOT exclude
     * soft-deleted resumes: those endpoints are how a recruiter/admin opens the
     * exact resume a candidate applied with, which must keep working even after
     * the candidate deletes it from their own history (see deleteResume above).
     * "Deleted" only hides a resume from the owning student's own listings.
     */
    private Resume requireResume(Long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found: " + resumeId));
    }

    private ResumeResponse toResponse(Resume resume, AtsAnalysisResult analysis) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .userId(resume.getUser().getId())
                .originalFileName(resume.getOriginalFileName())
                .fileType(resume.getFileType())
                .fileSizeBytes(resume.getFileSizeBytes())
                .atsScore(resume.getAtsScore())
                .skills(resume.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
                .missingCoreSkills(analysis.getMissingCoreSkills())
                .suggestions(analysis.getSuggestions())
                .uploadedAt(resume.getCreatedAt())
                .build();
    }
}
