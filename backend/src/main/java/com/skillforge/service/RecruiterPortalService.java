package com.skillforge.service;

import com.skillforge.dto.JobResponse;
import com.skillforge.dto.NotificationResponse;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.InterviewSchedule;
import com.skillforge.model.Job;
import com.skillforge.model.JobApplication;
import com.skillforge.model.NotificationChannel;
import com.skillforge.model.NotificationType;
import com.skillforge.model.OfferLetter;
import com.skillforge.repository.InterviewScheduleRepository;
import com.skillforge.repository.JobApplicationRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.OfferLetterRepository;
import com.skillforge.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecruiterPortalService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewScheduleRepository interviewScheduleRepository;
    private final OfferLetterRepository offerLetterRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public RecruiterPortalService(JobRepository jobRepository, JobApplicationRepository jobApplicationRepository, InterviewScheduleRepository interviewScheduleRepository, OfferLetterRepository offerLetterRepository, NotificationService notificationService, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.interviewScheduleRepository = interviewScheduleRepository;
        this.offerLetterRepository = offerLetterRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    public List<JobResponse> jobs(Long recruiterId) {
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId).stream().map(job -> JobResponse.from(jobRepository.findDetailedById(job.getId()).orElseThrow())).toList();
    }

    public List<JobApplication> applicants(Long recruiterId, Long jobId) {
        ensureOwnJob(recruiterId, jobId);
        return jobApplicationRepository.findByJobIdOrderByAppliedAtDesc(jobId);
    }

    public List<JobApplication> allApplicants(Long recruiterId) {
        List<Long> jobIds = jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId).stream().map(Job::getId).toList();
        if (jobIds.isEmpty()) {
            return List.of();
        }
        return jobApplicationRepository.findByJobIdIn(jobIds);
    }

    public List<JobApplication> rankedApplicants(Long recruiterId, Long jobId) {
        ensureOwnJob(recruiterId, jobId);
        return jobApplicationRepository.findByJobIdOrderByAppliedAtDesc(jobId).stream()
                .sorted((left, right) -> Integer.compare(scoreApplicant(right), scoreApplicant(left)))
                .toList();
    }

    public List<JobApplication> applicantsByStatus(Long recruiterId, Long jobId, ApplicationStatus status) {
        ensureOwnJob(recruiterId, jobId);
        return jobApplicationRepository.findByJobIdOrderByAppliedAtDesc(jobId).stream().filter(application -> application.getStatus() == status).toList();
    }

    public long countApplicants(Long recruiterId, Long jobId) {
        ensureOwnJob(recruiterId, jobId);
        return jobApplicationRepository.findByJobIdOrderByAppliedAtDesc(jobId).size();
    }

    @Transactional
    public JobApplication shortlist(Long recruiterId, Long applicationId) {
        JobApplication application = loadApplication(applicationId);
        ensureOwnJob(recruiterId, application.getJob().getId());
        application.setStatus(ApplicationStatus.SHORTLISTED);
        jobApplicationRepository.save(application);
        notifyStudent(application, NotificationType.SHORTLIST_UPDATE, "Your application has been shortlisted");
        return application;
    }

    @Transactional
    public JobApplication reject(Long recruiterId, Long applicationId) {
        JobApplication application = loadApplication(applicationId);
        ensureOwnJob(recruiterId, application.getJob().getId());
        application.setStatus(ApplicationStatus.REJECTED);
        jobApplicationRepository.save(application);
        notifyStudent(application, NotificationType.APPLICATION_UPDATE, "Your application was not selected");
        return application;
    }

    @Transactional
    public InterviewSchedule scheduleInterview(Long recruiterId, Long applicationId, LocalDateTime interviewAt, String meetingLink, String notes) {
        JobApplication application = loadApplication(applicationId);
        ensureOwnJob(recruiterId, application.getJob().getId());
        application.setStatus(ApplicationStatus.INTERVIEW);
        jobApplicationRepository.save(application);
        InterviewSchedule interviewSchedule = new InterviewSchedule();
        interviewSchedule.setStudentId(application.getStudentId());
        interviewSchedule.setJob(application.getJob());
        interviewSchedule.setInterviewAt(interviewAt);
        interviewSchedule.setMeetingLink(meetingLink);
        interviewSchedule.setNotes(notes);
        InterviewSchedule saved = interviewScheduleRepository.save(interviewSchedule);
        notifyStudent(application, NotificationType.INTERVIEW_REMINDER, "Interview scheduled for " + interviewAt);
        return saved;
    }

    @Transactional
    public OfferLetter createOffer(Long recruiterId, Long applicationId, String offerTitle, String offerDescription) {
        JobApplication application = loadApplication(applicationId);
        ensureOwnJob(recruiterId, application.getJob().getId());
        application.setStatus(ApplicationStatus.OFFER);
        jobApplicationRepository.save(application);
        OfferLetter offerLetter = new OfferLetter();
        offerLetter.setStudentId(application.getStudentId());
        offerLetter.setJob(application.getJob());
        offerLetter.setOfferTitle(offerTitle);
        offerLetter.setOfferDescription(offerDescription);
        OfferLetter saved = offerLetterRepository.save(offerLetter);
        notifyStudent(application, NotificationType.OFFER_NOTIFICATION, offerTitle);
        return saved;
    }

    @Transactional
    public OfferLetter withdrawOffer(Long recruiterId, Long applicationId) {
        JobApplication application = loadApplication(applicationId);
        ensureOwnJob(recruiterId, application.getJob().getId());
        OfferLetter offerLetter = offerLetterRepository.findByStudentIdAndJobId(application.getStudentId(), application.getJob().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));
        offerLetterRepository.delete(offerLetter);
        application.setStatus(ApplicationStatus.REJECTED);
        jobApplicationRepository.save(application);
        notifyStudent(application, NotificationType.APPLICATION_UPDATE, "Offer withdrawn");
        return offerLetter;
    }

    public List<InterviewSchedule> interviews(Long recruiterId) {
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId).stream().flatMap(job -> interviewScheduleRepository.findByJobIdOrderByInterviewAtDesc(job.getId()).stream()).toList();
    }

    public List<OfferLetter> offers(Long recruiterId) {
        List<Long> jobIds = jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId).stream().map(Job::getId).toList();
        if (jobIds.isEmpty()) {
            return List.of();
        }
        return offerLetterRepository.findByJobIdIn(jobIds);
    }

    private void ensureOwnJob(Long recruiterId, Long jobId) {
        if (!jobRepository.existsByIdAndRecruiterId(jobId, recruiterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own jobs");
        }
    }

    private JobApplication loadApplication(Long applicationId) {
        return jobApplicationRepository.findById(applicationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private void notifyStudent(JobApplication application, NotificationType type, String title) {
        String email = userRepository.findById(application.getStudentId()).map(user -> user.getEmail()).orElse(null);
        notificationService.create(application.getStudentId(), null, type, NotificationChannel.IN_APP, title, title, "jobApplication", application.getId(), email);
    }

    private int scoreApplicant(JobApplication application) {
        int score = switch (application.getStatus()) {
            case OFFER -> 100;
            case INTERVIEW -> 85;
            case SHORTLISTED -> 75;
            case APPLIED -> 50;
            case REJECTED -> 0;
        };
        score += application.getAppliedAt() == null ? 0 : Math.max(0, 20 - (int) java.time.Duration.between(application.getAppliedAt(), LocalDateTime.now()).toDays());
        return score;
    }
}