package com.skillforge.service;

import com.skillforge.dto.JobResponse;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.JobApplication;
import com.skillforge.model.OfferLetter;
import com.skillforge.model.InterviewSchedule;
import com.skillforge.model.PlacementTimelineEntry;
import com.skillforge.model.SavedJob;
import com.skillforge.repository.JobApplicationRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.InterviewScheduleRepository;
import com.skillforge.repository.OfferLetterRepository;
import com.skillforge.repository.PlacementTimelineEntryRepository;
import com.skillforge.repository.SavedJobRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentPlacementService {

    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final OfferLetterRepository offerLetterRepository;
    private final PlacementTimelineEntryRepository placementTimelineEntryRepository;
    private final InterviewScheduleRepository interviewScheduleRepository;
    private final JobRepository jobRepository;

    public StudentPlacementService(JobApplicationRepository jobApplicationRepository, SavedJobRepository savedJobRepository, OfferLetterRepository offerLetterRepository, PlacementTimelineEntryRepository placementTimelineEntryRepository, InterviewScheduleRepository interviewScheduleRepository, JobRepository jobRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.savedJobRepository = savedJobRepository;
        this.offerLetterRepository = offerLetterRepository;
        this.placementTimelineEntryRepository = placementTimelineEntryRepository;
        this.interviewScheduleRepository = interviewScheduleRepository;
        this.jobRepository = jobRepository;
    }

    public List<JobApplication> appliedJobs(Long studentId) {
        return jobApplicationRepository.findByStudentIdOrderByAppliedAtDesc(studentId);
    }

    public List<SavedJob> savedJobs(Long studentId) {
        return savedJobRepository.findByStudentIdOrderBySavedAtDesc(studentId);
    }

    public List<InterviewSchedule> interviewSchedule(Long studentId) {
        return interviewScheduleRepository.findByStudentIdOrderByInterviewAtDesc(studentId);
    }

    public List<OfferLetter> offers(Long studentId) {
        return offerLetterRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public List<PlacementTimelineEntry> timeline(Long studentId) {
        return placementTimelineEntryRepository.findByStudentIdOrderByOccurredAtDesc(studentId);
    }

    public List<JobApplication> applicationsByStatus(Long studentId, ApplicationStatus status) {
        return jobApplicationRepository.findByStudentIdOrderByAppliedAtDesc(studentId).stream().filter(application -> application.getStatus() == status).toList();
    }

    public long countSavedJobs(Long studentId) {
        return savedJobRepository.findByStudentIdOrderBySavedAtDesc(studentId).size();
    }

    public long countAppliedJobs(Long studentId) {
        return jobApplicationRepository.countByStudentId(studentId);
    }

    @Transactional
    public SavedJob saveJob(Long studentId, Long jobId) {
        if (savedJobRepository.existsByStudentIdAndJobId(studentId, jobId)) {
            return savedJobRepository.findByStudentIdAndJobId(studentId, jobId).orElseThrow();
        }
        SavedJob savedJob = new SavedJob();
        savedJob.setStudentId(studentId);
        savedJob.setJob(jobRepository.findById(jobId).orElseThrow());
        return savedJobRepository.save(savedJob);
    }

    @Transactional
    public void removeSavedJob(Long studentId, Long jobId) {
        savedJobRepository.findByStudentIdAndJobId(studentId, jobId).ifPresent(savedJobRepository::delete);
    }

    public void recordTimeline(Long studentId, Long jobId, ApplicationStatus status, String description) {
        PlacementTimelineEntry entry = new PlacementTimelineEntry();
        entry.setStudentId(studentId);
        entry.setJobId(jobId);
        entry.setStatus(status);
        entry.setDescription(description);
        placementTimelineEntryRepository.save(entry);
    }
}