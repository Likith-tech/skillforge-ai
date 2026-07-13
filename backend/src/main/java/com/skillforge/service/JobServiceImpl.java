package com.skillforge.service;

import com.skillforge.dto.JobRequest;
import com.skillforge.dto.JobResponse;
import com.skillforge.dto.PageResponse;
import com.skillforge.dto.SavedJobResponse;
import com.skillforge.exception.BadRequestException;
import com.skillforge.exception.DuplicateResourceException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.Company;
import com.skillforge.model.ExperienceLevel;
import com.skillforge.model.Job;
import com.skillforge.model.JobStatus;
import com.skillforge.model.JobType;
import com.skillforge.model.SavedJob;
import com.skillforge.model.Skill;
import com.skillforge.model.User;
import com.skillforge.repository.ApplicationRepository;
import com.skillforge.repository.CompanyRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.SavedJobRepository;
import com.skillforge.repository.SkillRepository;
import com.skillforge.repository.UserRepository;
import com.skillforge.util.PageRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final SavedJobRepository savedJobRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public JobResponse createJob(Long recruiterId, JobRequest request) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + recruiterId));

        JobType type = parseJobType(request.getType());
        ExperienceLevel experienceLevel = parseExperienceLevel(request.getExperienceLevel());
        Set<Skill> requiredSkills = resolveSkills(request.getRequiredSkills());
        Company companyProfile = companyRepository.findByOwnerId(recruiterId).orElse(null);

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .company(request.getCompany())
                .companyProfile(companyProfile)
                .location(request.getLocation())
                .type(type)
                .experienceLevel(experienceLevel)
                .salaryRange(request.getSalaryRange())
                .postedBy(recruiter)
                .requiredSkills(requiredSkills)
                .build();

        jobRepository.save(job);
        return toResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchJobs(String keyword, String location, String type, String experienceLevel,
                                                 String skill, String company, Integer page, Integer size, String sort) {
        Specification<Job> spec = Specification.where(JobSpecifications.hasStatus(JobStatus.OPEN));

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(JobSpecifications.keywordMatches(keyword));
        }
        if (location != null && !location.isBlank()) {
            spec = spec.and(JobSpecifications.hasLocation(location));
        }
        if (type != null && !type.isBlank()) {
            spec = spec.and(JobSpecifications.hasType(parseJobType(type)));
        }
        if (experienceLevel != null && !experienceLevel.isBlank()) {
            spec = spec.and(JobSpecifications.hasExperienceLevel(parseExperienceLevel(experienceLevel)));
        }
        if (skill != null && !skill.isBlank()) {
            spec = spec.and(JobSpecifications.requiresSkill(skill));
        }
        if (company != null && !company.isBlank()) {
            spec = spec.and(JobSpecifications.hasCompany(company));
        }

        Pageable pageable = PageRequestFactory.of(page, size, resolveJobSort(sort));
        return PageResponse.from(fetchAndHydrate(jobRepository.findAll(spec, pageable)));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> listJobsForRecruiter(Long recruiterId, Integer page, Integer size, String sort) {
        Pageable pageable = PageRequestFactory.of(page, size, resolveJobSort(sort));
        return PageResponse.from(fetchAndHydrate(jobRepository.findByPostedById(recruiterId, pageable)));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> listAllJobs(Integer page, Integer size, String sort) {
        Pageable pageable = PageRequestFactory.of(page, size, resolveJobSort(sort));
        return PageResponse.from(fetchAndHydrate(jobRepository.findAll(pageable)));
    }

    /**
     * Two-step read to avoid N+1: the paginated query above only touches the jobs
     * table (correct total count, no lazy fields). This second query hydrates just
     * that page's rows with postedBy + requiredSkills fetched in one shot via
     * @EntityGraph, instead of Hibernate lazily loading them one row at a time as
     * toResponse() would otherwise trigger. Bounded by page size, not dataset size.
     */
    private Page<JobResponse> fetchAndHydrate(Page<Job> basePage) {
        List<Long> ids = basePage.getContent().stream().map(Job::getId).toList();
        Map<Long, Job> hydrated = jobRepository.findByIdIn(ids).stream()
                .collect(Collectors.toMap(Job::getId, job -> job));
        return basePage.map(job -> toResponse(hydrated.get(job.getId())));
    }

    private Sort resolveJobSort(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortKey) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "companyName" -> Sort.by(Sort.Direction.ASC, "company");
            case "jobTitle" -> Sort.by(Sort.Direction.ASC, "title");
            case "salary" -> Sort.by(Sort.Direction.ASC, "salaryRange");
            default -> throw new BadRequestException(
                    "Invalid sort key: " + sortKey + ". Valid values: newest, oldest, companyName, jobTitle, salary");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJob(Long jobId) {
        return toResponse(getJobEntityOrThrow(jobId));
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, JobRequest request) {
        Job job = getJobEntityOrThrow(jobId);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setType(parseJobType(request.getType()));
        job.setExperienceLevel(parseExperienceLevel(request.getExperienceLevel()));
        job.setSalaryRange(request.getSalaryRange());
        job.setRequiredSkills(resolveSkills(request.getRequiredSkills()));

        jobRepository.save(job);
        return toResponse(job);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        Job job = getJobEntityOrThrow(jobId);
        if (applicationRepository.existsByJobId(jobId)) {
            throw new BadRequestException(
                    "This job already has applications and can't be deleted - deactivate it instead");
        }
        jobRepository.delete(job);
    }

    @Override
    @Transactional
    public JobResponse setStatus(Long jobId, String status) {
        Job job = getJobEntityOrThrow(jobId);
        job.setStatus(parseJobStatus(status));
        jobRepository.save(job);
        return toResponse(job);
    }

    @Override
    @Transactional
    public void saveJob(Long studentId, Long jobId) {
        if (savedJobRepository.existsByStudentIdAndJobId(studentId, jobId)) {
            throw new DuplicateResourceException("You've already saved this job");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + studentId));
        Job job = getJobEntityOrThrow(jobId);

        savedJobRepository.save(SavedJob.builder().student(student).job(job).build());
    }

    @Override
    @Transactional
    public void unsaveJob(Long studentId, Long jobId) {
        SavedJob savedJob = savedJobRepository.findByStudentIdAndJobId(studentId, jobId)
                .orElseThrow(() -> new ResourceNotFoundException("You haven't saved this job"));
        savedJobRepository.delete(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedJobResponse> listSavedJobs(Long studentId) {
        return savedJobRepository.findByStudentIdOrderBySavedAtDesc(studentId).stream()
                .map(saved -> SavedJobResponse.builder()
                        .id(saved.getId())
                        .job(toResponse(saved.getJob()))
                        .savedAt(saved.getSavedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Job getJobEntityOrThrow(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
    }

    @Override
    public JobResponse toDto(Job job) {
        return toResponse(job);
    }

    private Set<Skill> resolveSkills(List<String> names) {
        Set<Skill> skills = new HashSet<>();
        for (String rawName : names) {
            String name = rawName == null ? "" : rawName.trim();
            if (name.isEmpty()) {
                continue;
            }
            Skill skill = skillRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> skillRepository.save(new Skill(name)));
            skills.add(skill);
        }
        return skills;
    }

    private JobType parseJobType(String rawType) {
        String normalized = rawType.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        try {
            return JobType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid job type: " + rawType + ". Valid values: " + Arrays.toString(JobType.values()));
        }
    }

    private ExperienceLevel parseExperienceLevel(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        try {
            return ExperienceLevel.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid experience level: " + raw + ". Valid values: " + Arrays.toString(ExperienceLevel.values()));
        }
    }

    private JobStatus parseJobStatus(String raw) {
        String normalized = raw.trim().toUpperCase();
        try {
            return JobStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid job status: " + raw + ". Valid values: " + Arrays.toString(JobStatus.values()));
        }
    }

    private JobResponse toResponse(Job job) {
        Company companyProfile = job.getCompanyProfile();
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .company(job.getCompany())
                .companyId(companyProfile != null ? companyProfile.getId() : null)
                .companyWebsite(companyProfile != null ? companyProfile.getWebsite() : null)
                .companyLogo(companyProfile != null ? companyProfile.getLogo() : null)
                .location(job.getLocation())
                .type(job.getType().name())
                .experienceLevel(job.getExperienceLevel() != null ? job.getExperienceLevel().name() : null)
                .salaryRange(job.getSalaryRange())
                .status(job.getStatus().name())
                .postedById(job.getPostedBy().getId())
                .postedByName(job.getPostedBy().getFullName())
                .requiredSkills(job.getRequiredSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
                .createdAt(job.getCreatedAt())
                .build();
    }
}
