package com.skillforge.service;

import com.skillforge.dto.ResumeResponse;
import com.skillforge.dto.ResumeVersionResponse;
import com.skillforge.model.Resume;
import com.skillforge.model.ResumeStatus;
import com.skillforge.model.ResumeVersion;
import com.skillforge.repository.ResumeRepository;
import com.skillforge.repository.ResumeVersionRepository;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeStorageService resumeStorageService;
    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeService(
            ResumeRepository resumeRepository,
            ResumeVersionRepository resumeVersionRepository,
            ResumeStorageService resumeStorageService,
            ResumeAnalysisService resumeAnalysisService
    ) {
        this.resumeRepository = resumeRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.resumeStorageService = resumeStorageService;
        this.resumeAnalysisService = resumeAnalysisService;
    }

    public ResumeResponse upload(Long studentId, MultipartFile file) {
        if (resumeRepository.existsByStudentIdAndStatus(studentId, ResumeStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Resume already exists. Use replace instead.");
        }

        StoredResumeFile storedFile = resumeStorageService.store(file, studentId);
        Resume resume = new Resume();
        resume.setStudentId(studentId);
        applyStoredFile(resume, storedFile);

        Resume savedResume = resumeRepository.save(resume);
        ResumeVersion version = resumeVersionRepository.save(createVersion(savedResume, 1));
        resumeAnalysisService.analyzeAndPersist(savedResume, version);
        return ResumeResponse.from(savedResume);
    }

    public ResumeResponse replace(Long studentId, MultipartFile file) {
        Resume resume = findActiveResume(studentId);
        StoredResumeFile storedFile = resumeStorageService.store(file, studentId);
        applyStoredFile(resume, storedFile);

        Resume savedResume = resumeRepository.save(resume);
        int nextVersion = resumeVersionRepository.findTopByStudentIdOrderByVersionNumberDesc(studentId)
                .map(version -> version.getVersionNumber() + 1)
                .orElse(1);
        ResumeVersion version = resumeVersionRepository.save(createVersion(savedResume, nextVersion));
        resumeAnalysisService.analyzeAndPersist(savedResume, version);
        return ResumeResponse.from(savedResume);
    }

    public ResumeResponse getCurrent(Long studentId) {
        return ResumeResponse.from(findActiveResume(studentId));
    }

    public List<ResumeVersionResponse> getHistory(Long studentId) {
        return resumeVersionRepository.findByStudentIdOrderByVersionNumberDesc(studentId)
                .stream()
                .map(ResumeVersionResponse::from)
                .toList();
    }

    public void delete(Long studentId) {
        Resume resume = findActiveResume(studentId);
        resume.setStatus(ResumeStatus.DELETED);
        resumeRepository.save(resume);
    }

    public ResumeFileResource getCurrentFile(Long studentId, boolean preview) {
        Resume resume = findActiveResume(studentId);
        if (preview && !"application/pdf".equals(resume.getFileType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preview is available only for PDF resumes");
        }

        Resource resource = resumeStorageService.loadAsResource(resume.getFilePath());
        return new ResumeFileResource(resource, resume.getOriginalFileName(), resume.getFileType(), resume.getFileSize());
    }

    public ResumeFileResource getVersionFile(Long studentId, Long versionId, boolean preview) {
        ResumeVersion version = resumeVersionRepository.findById(versionId)
                .filter(item -> item.getStudentId().equals(studentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume version not found"));

        if (preview && !"application/pdf".equals(version.getFileType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preview is available only for PDF resumes");
        }

        Resource resource = resumeStorageService.loadAsResource(version.getFilePath());
        return new ResumeFileResource(resource, version.getOriginalFileName(), version.getFileType(), version.getFileSize());
    }

    private Resume findActiveResume(Long studentId) {
        return resumeRepository.findByStudentIdAndStatus(studentId, ResumeStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active resume not found"));
    }

    private void applyStoredFile(Resume resume, StoredResumeFile storedFile) {
        resume.setFileName(storedFile.getFileName());
        resume.setOriginalFileName(storedFile.getOriginalFileName());
        resume.setFileType(storedFile.getFileType());
        resume.setFileSize(storedFile.getFileSize());
        resume.setFilePath(storedFile.getFilePath());
        resume.setStatus(ResumeStatus.ACTIVE);
    }

    private ResumeVersion createVersion(Resume resume, int versionNumber) {
        ResumeVersion version = new ResumeVersion();
        version.setResumeId(resume.getId());
        version.setStudentId(resume.getStudentId());
        version.setVersionNumber(versionNumber);
        version.setFileName(resume.getFileName());
        version.setOriginalFileName(resume.getOriginalFileName());
        version.setFileType(resume.getFileType());
        version.setFileSize(resume.getFileSize());
        version.setFilePath(resume.getFilePath());
        version.setStatus(ResumeStatus.ACTIVE);
        return version;
    }
}
