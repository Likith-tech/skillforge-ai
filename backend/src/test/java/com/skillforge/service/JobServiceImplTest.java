package com.skillforge.service;

import com.skillforge.dto.JobRequest;
import com.skillforge.dto.JobResponse;
import com.skillforge.dto.PageResponse;
import com.skillforge.exception.BadRequestException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.Job;
import com.skillforge.model.JobStatus;
import com.skillforge.model.User;
import com.skillforge.repository.ApplicationRepository;
import com.skillforge.repository.CompanyRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.SavedJobRepository;
import com.skillforge.repository.SkillRepository;
import com.skillforge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock private JobRepository jobRepository;
    @Mock private UserRepository userRepository;
    @Mock private SkillRepository skillRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private SavedJobRepository savedJobRepository;
    @Mock private ApplicationRepository applicationRepository;

    private JobServiceImpl jobService;

    @BeforeEach
    void setUp() {
        jobService = new JobServiceImpl(jobRepository, userRepository, skillRepository, companyRepository,
                savedJobRepository, applicationRepository);
    }

    private User recruiter(long id) {
        return User.builder().id(id).fullName("Recruiter " + id).email("r" + id + "@example.com").build();
    }

    private Job job(long id, User postedBy) {
        return Job.builder().id(id).title("Backend Engineer").description("desc").company("Acme")
                .location("Remote").type(com.skillforge.model.JobType.FULL_TIME).status(JobStatus.OPEN)
                .postedBy(postedBy).requiredSkills(Set.of()).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void createJob_savesJobOwnedByRecruiter() {
        User recruiter = recruiter(10L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(recruiter));
        when(companyRepository.findByOwnerId(10L)).thenReturn(Optional.empty());
        when(skillRepository.findByNameIgnoreCase("Java")).thenReturn(Optional.empty());
        when(skillRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JobRequest request = new JobRequest("Backend Engineer", "desc", "Acme", "Remote", "FULL_TIME", "MID",
                "10-15 LPA", List.of("Java"));

        JobResponse response = jobService.createJob(10L, request);

        assertThat(response.getTitle()).isEqualTo("Backend Engineer");
        assertThat(response.getPostedById()).isEqualTo(10L);
    }

    @Test
    void createJob_unknownRecruiter_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        JobRequest request = new JobRequest("T", "D", "C", "L", "FULL_TIME", null, null, List.of("Java"));

        assertThatThrownBy(() -> jobService.createJob(99L, request)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createJob_invalidType_throwsBadRequestException() {
        User recruiter = recruiter(10L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(recruiter));
        JobRequest request = new JobRequest("T", "D", "C", "L", "NOT_A_TYPE", null, null, List.of("Java"));

        assertThatThrownBy(() -> jobService.createJob(10L, request)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void searchJobs_hydratesPagedResultsAndPreservesTotals() {
        User recruiter = recruiter(10L);
        Job job1 = job(1L, recruiter);
        Job job2 = job(2L, recruiter);
        Page<Job> basePage = new PageImpl<>(List.of(job1, job2), PageRequest.of(0, 20), 2);

        when(jobRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(basePage);
        when(jobRepository.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(job1, job2));

        PageResponse<JobResponse> result = jobService.searchJobs(null, null, null, null, null, null, 0, 20, "newest");

        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.content()).extracting(JobResponse::getId).containsExactly(1L, 2L);
    }

    @Test
    void searchJobs_invalidSortKey_throwsBadRequestException() {
        assertThatThrownBy(() -> jobService.searchJobs(null, null, null, null, null, null, 0, 20, "bogus"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateJob_appliesEveryField() {
        User recruiter = recruiter(10L);
        Job existing = job(1L, recruiter);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(skillRepository.findByNameIgnoreCase("Python")).thenReturn(Optional.empty());
        when(skillRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JobRequest request = new JobRequest("Updated Title", "Updated desc", "Acme", "Hybrid", "CONTRACT",
                "SENIOR", "20-25 LPA", List.of("Python"));

        JobResponse response = jobService.updateJob(1L, request);

        assertThat(response.getTitle()).isEqualTo("Updated Title");
        assertThat(response.getType()).isEqualTo("CONTRACT");
    }

    @Test
    void deleteJob_withExistingApplications_throwsBadRequestException() {
        User recruiter = recruiter(10L);
        Job existing = job(1L, recruiter);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(applicationRepository.existsByJobId(1L)).thenReturn(true);

        assertThatThrownBy(() -> jobService.deleteJob(1L)).isInstanceOf(BadRequestException.class);
        verify(jobRepository, never()).delete(any(Job.class));
    }

    @Test
    void deleteJob_noApplications_deletesJob() {
        User recruiter = recruiter(10L);
        Job existing = job(1L, recruiter);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(applicationRepository.existsByJobId(1L)).thenReturn(false);

        jobService.deleteJob(1L);

        verify(jobRepository).delete((Job) existing);
    }

    @Test
    void setStatus_invalidValue_throwsBadRequestException() {
        User recruiter = recruiter(10L);
        Job existing = job(1L, recruiter);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> jobService.setStatus(1L, "PAUSED")).isInstanceOf(BadRequestException.class);
    }

    @Test
    void setStatus_valid_updatesJob() {
        User recruiter = recruiter(10L);
        Job existing = job(1L, recruiter);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(existing));

        JobResponse response = jobService.setStatus(1L, "CLOSED");

        assertThat(response.getStatus()).isEqualTo("CLOSED");
    }

    @Test
    void saveJob_alreadySaved_throwsDuplicateResourceException() {
        when(savedJobRepository.existsByStudentIdAndJobId(1L, 5L)).thenReturn(true);

        assertThatThrownBy(() -> jobService.saveJob(1L, 5L))
                .isInstanceOf(com.skillforge.exception.DuplicateResourceException.class);
    }

    @Test
    void getJobEntityOrThrow_unknownId_throwsResourceNotFoundException() {
        when(jobRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.getJobEntityOrThrow(404L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void toDto_mapsEntityFieldsWithoutHittingTheDatabase() {
        Job job = job(1L, recruiter(10L));

        JobResponse dto = jobService.toDto(job);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getPostedByName()).isEqualTo("Recruiter 10");
    }
}
