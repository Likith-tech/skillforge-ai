package com.skillforge.service;

import com.skillforge.dto.JobListResponse;
import com.skillforge.dto.JobRequest;
import com.skillforge.dto.JobResponse;
import com.skillforge.model.Company;
import com.skillforge.model.EmploymentType;
import com.skillforge.model.Job;
import com.skillforge.model.JobCategory;
import com.skillforge.model.JobLocation;
import com.skillforge.model.JobSkill;
import com.skillforge.model.JobSkillType;
import com.skillforge.model.JobStatus;
import com.skillforge.repository.CompanyRepository;
import com.skillforge.repository.JobCategoryRepository;
import com.skillforge.repository.JobLocationRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.JobSkillRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobLocationRepository jobLocationRepository;
    private final JobSkillRepository jobSkillRepository;

    public JobService(JobRepository jobRepository, CompanyRepository companyRepository, JobCategoryRepository jobCategoryRepository, JobLocationRepository jobLocationRepository, JobSkillRepository jobSkillRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.jobCategoryRepository = jobCategoryRepository;
        this.jobLocationRepository = jobLocationRepository;
        this.jobSkillRepository = jobSkillRepository;
    }

    @Transactional
    public JobResponse create(Long recruiterId, JobRequest request) {
        Company company = companyRepository.findByNameIgnoreCase(request.getCompanyName())
                .orElseGet(() -> companyRepository.save(createCompany(request.getCompanyName())));
        JobCategory category = jobCategoryRepository.findByNameIgnoreCase(request.getCategoryName())
                .orElseGet(() -> jobCategoryRepository.save(createCategory(request.getCategoryName())));
        JobLocation location = jobLocationRepository.findByDisplayNameIgnoreCase(request.getLocationDisplayName())
                .orElseGet(() -> jobLocationRepository.save(createLocation(request.getLocationDisplayName())));

        Job job = new Job();
        job.setRecruiterId(recruiterId);
        job.setCompany(company);
        job.setCategory(category);
        job.setLocation(location);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setEmploymentType(request.getEmploymentType());
        job.setMinimumCgpa(request.getMinimumCgpa());
        job.setMinimumExperienceYears(request.getMinimumExperienceYears());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setCurrency(request.getCurrency());
        job.setDeadline(request.getDeadline());
        job.setStatus(request.getStatus() == null ? JobStatus.ACTIVE : request.getStatus());

        Job saved = jobRepository.save(job);
        saveSkills(saved, request.getRequiredSkills(), request.getPreferredSkills());
        return JobResponse.from(jobRepository.findDetailedById(saved.getId()).orElseThrow());
    }

    public JobResponse getById(Long id) {
        return JobResponse.from(loadDetailed(id));
    }

    public JobListResponse list(String q, String category, String location, String employmentType, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 50)), buildSort(sort));
        Specification<Job> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), JobStatus.ACTIVE));
            if (q != null && !q.isBlank()) {
                String like = "%" + q.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("company").get("name")), like)
                ));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category").get("name")), category.toLowerCase(Locale.ROOT)));
            }
            if (location != null && !location.isBlank()) {
                predicates.add(cb.or(
                        cb.equal(cb.lower(root.get("location").get("displayName")), location.toLowerCase(Locale.ROOT)),
                        cb.equal(cb.lower(root.get("location").get("city")), location.toLowerCase(Locale.ROOT))
                ));
            }
            if (employmentType != null && !employmentType.isBlank()) {
                predicates.add(cb.equal(root.get("employmentType"), EmploymentType.valueOf(employmentType)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Job> jobs = jobRepository.findAll(specification, pageable);
        JobListResponse response = new JobListResponse();
        response.setItems(jobs.getContent().stream().map(job -> JobResponse.from(jobRepository.findDetailedById(job.getId()).orElseThrow())).toList());
        response.setPage(jobs.getNumber());
        response.setSize(jobs.getSize());
        response.setTotalItems(jobs.getTotalElements());
        response.setTotalPages(jobs.getTotalPages());
        return response;
    }

    public List<JobResponse> listByRecruiter(Long recruiterId) {
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId).stream()
                .map(job -> JobResponse.from(loadDetailed(job.getId())))
                .toList();
    }

    @Transactional
    public JobResponse update(Long recruiterId, Long jobId, JobRequest request) {
        Job job = loadOwnedJob(recruiterId, jobId);
        Company company = companyRepository.findByNameIgnoreCase(request.getCompanyName())
                .orElseGet(() -> companyRepository.save(createCompany(request.getCompanyName())));
        JobCategory category = jobCategoryRepository.findByNameIgnoreCase(request.getCategoryName())
                .orElseGet(() -> jobCategoryRepository.save(createCategory(request.getCategoryName())));
        JobLocation location = jobLocationRepository.findByDisplayNameIgnoreCase(request.getLocationDisplayName())
                .orElseGet(() -> jobLocationRepository.save(createLocation(request.getLocationDisplayName())));

        job.setCompany(company);
        job.setCategory(category);
        job.setLocation(location);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setEmploymentType(request.getEmploymentType());
        job.setMinimumCgpa(request.getMinimumCgpa());
        job.setMinimumExperienceYears(request.getMinimumExperienceYears());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setCurrency(request.getCurrency());
        job.setDeadline(request.getDeadline());
        job.setStatus(request.getStatus() == null ? JobStatus.ACTIVE : request.getStatus());

        Job saved = jobRepository.save(job);
        jobSkillRepository.deleteAll(jobSkillRepository.findByJobIdOrderByPriorityRankAsc(jobId));
        saveSkills(saved, request.getRequiredSkills(), request.getPreferredSkills());
        return JobResponse.from(loadDetailed(saved.getId()));
    }

    @Transactional
    public void updateStatus(Long recruiterId, Long jobId, JobStatus status) {
        Job job = loadOwnedJob(recruiterId, jobId);
        job.setStatus(status);
        jobRepository.save(job);
    }

    private void saveSkills(Job job, List<JobRequest.JobSkillRequest> requiredSkills, List<JobRequest.JobSkillRequest> preferredSkills) {
        List<JobSkill> skills = new ArrayList<>();
        addSkills(skills, job, requiredSkills, JobSkillType.REQUIRED);
        addSkills(skills, job, preferredSkills, JobSkillType.PREFERRED);
        jobSkillRepository.saveAll(skills);
    }

    private void addSkills(List<JobSkill> skills, Job job, List<JobRequest.JobSkillRequest> requests, JobSkillType type) {
        if (requests == null) {
            return;
        }
        int priority = 1;
        for (JobRequest.JobSkillRequest request : requests) {
            JobSkill skill = new JobSkill();
            skill.setJob(job);
            skill.setSkillName(request.getSkillName().trim());
            skill.setSkillType(type);
            skill.setPriorityRank(request.getPriorityRank() == null ? priority : request.getPriorityRank());
            skills.add(skill);
            priority++;
        }
    }

    private Job loadOwnedJob(Long recruiterId, Long jobId) {
        Job job = loadDetailed(jobId);
        if (!job.getRecruiterId().equals(recruiterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own jobs");
        }
        return job;
    }

    private Job loadDetailed(Long jobId) {
        return jobRepository.findDetailedById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort.toLowerCase(Locale.ROOT)) {
            case "deadline" -> Sort.by(Sort.Direction.ASC, "deadline");
            case "salary" -> Sort.by(Sort.Direction.DESC, "salaryMax");
            case "title" -> Sort.by(Sort.Direction.ASC, "title");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    private Company createCompany(String name) {
        Company company = new Company();
        company.setName(name.trim());
        return company;
    }

    private JobCategory createCategory(String name) {
        JobCategory category = new JobCategory();
        category.setName(name.trim());
        return category;
    }

    private JobLocation createLocation(String displayName) {
        JobLocation location = new JobLocation();
        location.setDisplayName(displayName.trim());
        location.setCity(displayName.trim());
        location.setState(displayName.trim());
        location.setCountry("India");
        location.setRemoteFriendly(displayName.toLowerCase(Locale.ROOT).contains("remote"));
        return location;
    }
}