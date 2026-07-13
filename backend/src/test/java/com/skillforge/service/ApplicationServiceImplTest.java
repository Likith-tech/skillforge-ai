package com.skillforge.service;

import com.skillforge.dto.ApplicationRequest;
import com.skillforge.dto.ApplicationResponse;
import com.skillforge.dto.PageResponse;
import com.skillforge.exception.BadRequestException;
import com.skillforge.model.Application;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.Job;
import com.skillforge.model.JobStatus;
import com.skillforge.model.Resume;
import com.skillforge.model.User;
import com.skillforge.repository.ApplicationRepository;
import com.skillforge.repository.ResumeRepository;
import com.skillforge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private UserRepository userRepository;
    @Mock private JobService jobService;
    @Mock private JobMatchingService jobMatchingService;

    private ApplicationServiceImpl applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new ApplicationServiceImpl(applicationRepository, resumeRepository, userRepository,
                jobService, jobMatchingService);
    }

    private User user(long id) {
        return User.builder().id(id).fullName("User " + id).email("u" + id + "@example.com").build();
    }

    private Job job(long id, JobStatus status) {
        return Job.builder().id(id).title("Backend Engineer").company("Acme").status(status)
                .postedBy(user(99L)).requiredSkills(Set.of()).build();
    }

    private Resume resume(long id, User owner) {
        return Resume.builder().id(id).user(owner).originalFileName("resume.pdf").fileType("PDF")
                .skills(Set.of()).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void apply_success_computesMatchScoreAndSaves() {
        Job job = job(1L, JobStatus.OPEN);
        User student = user(2L);
        Resume resume = resume(3L, student);

        when(jobService.getJobEntityOrThrow(1L)).thenReturn(job);
        when(applicationRepository.existsByJobIdAndStudentId(1L, 2L)).thenReturn(false);
        when(resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(2L)).thenReturn(Optional.of(resume));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));
        when(jobMatchingService.computeMatchScore(job, resume.getSkills())).thenReturn(80);

        ApplicationResponse response = applicationService.apply(2L, new ApplicationRequest(1L));

        assertThat(response.getMatchScore()).isEqualTo(80);
        assertThat(response.getStatus()).isEqualTo("APPLIED");
    }

    @Test
    void apply_jobNotOpen_throwsBadRequestException() {
        Job closedJob = job(1L, JobStatus.CLOSED);
        when(jobService.getJobEntityOrThrow(1L)).thenReturn(closedJob);

        assertThatThrownBy(() -> applicationService.apply(2L, new ApplicationRequest(1L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("no longer accepting");
    }

    @Test
    void apply_alreadyApplied_throwsBadRequestException() {
        Job job = job(1L, JobStatus.OPEN);
        when(jobService.getJobEntityOrThrow(1L)).thenReturn(job);
        when(applicationRepository.existsByJobIdAndStudentId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> applicationService.apply(2L, new ApplicationRequest(1L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already applied");
    }

    @Test
    void apply_noResumeUploaded_throwsBadRequestException() {
        Job job = job(1L, JobStatus.OPEN);
        when(jobService.getJobEntityOrThrow(1L)).thenReturn(job);
        when(applicationRepository.existsByJobIdAndStudentId(1L, 2L)).thenReturn(false);
        when(resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.apply(2L, new ApplicationRequest(1L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Upload a resume");
    }

    @Test
    void listForStudent_returnsHydratedPage() {
        Job job = job(1L, JobStatus.OPEN);
        User student = user(2L);
        Resume resume = resume(3L, student);
        Application application = Application.builder().id(1L).job(job).student(student).resume(resume)
                .matchScore(50).status(ApplicationStatus.APPLIED).createdAt(LocalDateTime.now()).build();

        when(applicationRepository.findByStudentId(eq(2L), any()))
                .thenReturn(new PageImpl<>(List.of(application), PageRequest.of(0, 10), 1));

        PageResponse<ApplicationResponse> page = applicationService.listForStudent(2L, 0, 10, "newest");

        assertThat(page.totalElements()).isEqualTo(1);
        assertThat(page.content().get(0).getJobTitle()).isEqualTo("Backend Engineer");
    }

    @Test
    void listForJob_withStatusFilter_usesFilteredQuery() {
        Job job = job(1L, JobStatus.OPEN);
        when(applicationRepository.findByJobIdAndStatus(eq(1L), eq(ApplicationStatus.SHORTLISTED), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        PageResponse<ApplicationResponse> page = applicationService.listForJob(1L, "shortlisted", 0, 10, "newest");

        assertThat(page.totalElements()).isEqualTo(0);
    }

    @Test
    void listForJob_noStatusFilter_usesUnfilteredQuery() {
        when(applicationRepository.findByJobId(eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        PageResponse<ApplicationResponse> page = applicationService.listForJob(1L, null, 0, 10, "newest");

        assertThat(page.totalElements()).isEqualTo(0);
    }

    @Test
    void listForStudent_invalidSortKey_throwsBadRequestException() {
        assertThatThrownBy(() -> applicationService.listForStudent(2L, 0, 10, "bogus"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateStatus_invalidValue_throwsBadRequestException() {
        Application application = Application.builder().id(1L).job(job(1L, JobStatus.OPEN)).student(user(2L))
                .resume(resume(3L, user(2L))).status(ApplicationStatus.APPLIED).build();
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> applicationService.updateStatus(1L, "NOT_A_STATUS"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateStatus_valid_updatesAndReturnsNewStatus() {
        Application application = Application.builder().id(1L).job(job(1L, JobStatus.OPEN)).student(user(2L))
                .resume(resume(3L, user(2L))).status(ApplicationStatus.APPLIED).build();
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        ApplicationResponse response = applicationService.updateStatus(1L, "shortlisted");

        assertThat(response.getStatus()).isEqualTo("SHORTLISTED");
    }
}
