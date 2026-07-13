package com.skillforge.service;

import com.skillforge.dto.ApplicationRequest;
import com.skillforge.dto.ApplicationResponse;
import com.skillforge.dto.PageResponse;
import com.skillforge.exception.BadRequestException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.Application;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.Job;
import com.skillforge.model.JobStatus;
import com.skillforge.model.Resume;
import com.skillforge.model.User;
import com.skillforge.repository.ApplicationRepository;
import com.skillforge.repository.ResumeRepository;
import com.skillforge.repository.UserRepository;
import com.skillforge.util.PageRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobService jobService;
    private final JobMatchingService jobMatchingService;

    @Override
    @Transactional
    public ApplicationResponse apply(Long studentId, ApplicationRequest request) {
        Job job = jobService.getJobEntityOrThrow(request.getJobId());

        if (job.getStatus() != JobStatus.OPEN) {
            throw new BadRequestException("This job is no longer accepting applications");
        }
        if (applicationRepository.existsByJobIdAndStudentId(job.getId(), studentId)) {
            throw new BadRequestException("You have already applied to this job");
        }

        Resume resume = resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(studentId)
                .orElseThrow(() -> new BadRequestException("Upload a resume before applying to jobs"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + studentId));

        int matchScore = jobMatchingService.computeMatchScore(job, resume.getSkills());

        Application application = Application.builder()
                .job(job)
                .student(student)
                .resume(resume)
                .matchScore(matchScore)
                .status(ApplicationStatus.APPLIED)
                .build();

        applicationRepository.save(application);
        return toResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> listForStudent(Long studentId, Integer page, Integer size, String sort) {
        Pageable pageable = PageRequestFactory.of(page, size, resolveApplicationSort(sort));
        Page<Application> applications = applicationRepository.findByStudentId(studentId, pageable);
        return PageResponse.from(applications.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> listForJob(Long jobId, String status, Integer page, Integer size, String sort) {
        jobService.getJobEntityOrThrow(jobId);
        Pageable pageable = PageRequestFactory.of(page, size, resolveApplicationSort(sort));

        Page<Application> applications = (status == null || status.isBlank())
                ? applicationRepository.findByJobId(jobId, pageable)
                : applicationRepository.findByJobIdAndStatus(jobId, parseStatus(status), pageable);

        return PageResponse.from(applications.map(this::toResponse));
    }

    private Sort resolveApplicationSort(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortKey) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "highestScore" -> Sort.by(Sort.Direction.DESC, "resume.atsScore");
            case "jobTitle" -> Sort.by(Sort.Direction.ASC, "job.title");
            default -> throw new BadRequestException(
                    "Invalid sort key: " + sortKey + ". Valid values: newest, oldest, highestScore, jobTitle");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getById(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));
        return toResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, String status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        application.setStatus(parseStatus(status));
        applicationRepository.save(application);
        return toResponse(application);
    }

    private ApplicationStatus parseStatus(String raw) {
        String normalized = raw.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        try {
            return ApplicationStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid status: " + raw + ". Valid values: " + Arrays.toString(ApplicationStatus.values()));
        }
    }

    private ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .company(application.getJob().getCompany())
                .studentId(application.getStudent().getId())
                .studentName(application.getStudent().getFullName())
                .studentEmail(application.getStudent().getEmail())
                .resumeId(application.getResume().getId())
                .resumeFileName(application.getResume().getOriginalFileName())
                .resumeFileType(application.getResume().getFileType())
                .atsScore(application.getResume().getAtsScore())
                .matchScore(application.getMatchScore())
                .status(application.getStatus().name())
                .appliedAt(application.getCreatedAt())
                .build();
    }
}
