package com.skillforge.service;

import com.skillforge.dto.JobGapRecommendationResponse;
import com.skillforge.dto.JobRecommendationResponse;
import com.skillforge.dto.LearningRoadmapResponse;
import com.skillforge.dto.LearningRoadmapStepResponse;
import com.skillforge.dto.PlacementScoreResponse;
import com.skillforge.dto.SkillGapResponse;
import com.skillforge.dto.StudentDashboardResponse;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.Job;
import com.skillforge.model.JobSkill;
import com.skillforge.model.JobSkillType;
import com.skillforge.model.LearningRoadmap;
import com.skillforge.model.PlacementScore;
import com.skillforge.model.ParsedResume;
import com.skillforge.model.ResumeAnalysis;
import com.skillforge.model.RoadmapSkillLevel;
import com.skillforge.model.RoadmapStep;
import com.skillforge.model.RoadmapStepStatus;
import com.skillforge.model.ResumeStatus;
import com.skillforge.model.ResumeVersion;
import com.skillforge.model.StudentDashboardMetric;
import com.skillforge.model.StudentJobRecommendation;
import com.skillforge.model.StudentSkillGap;
import com.skillforge.repository.JobApplicationRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.JobSkillRepository;
import com.skillforge.repository.LearningRoadmapRepository;
import com.skillforge.repository.PlacementScoreRepository;
import com.skillforge.repository.ParsedResumeRepository;
import com.skillforge.repository.RoadmapStepRepository;
import com.skillforge.repository.ResumeAnalysisRepository;
import com.skillforge.repository.ResumeCertificationRepository;
import com.skillforge.repository.ResumeEducationRepository;
import com.skillforge.repository.ResumeExperienceRepository;
import com.skillforge.repository.ResumeHistoryRepository;
import com.skillforge.repository.ResumeKeywordRepository;
import com.skillforge.repository.ResumeProjectRepository;
import com.skillforge.repository.ResumeSkillRepository;
import com.skillforge.repository.ResumeVersionRepository;
import com.skillforge.repository.StudentDashboardMetricRepository;
import com.skillforge.repository.StudentJobRecommendationRepository;
import com.skillforge.repository.StudentSkillGapRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JobRecommendationService {

    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;
    private final ParsedResumeRepository parsedResumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeSkillRepository resumeSkillRepository;
    private final ResumeProjectRepository resumeProjectRepository;
    private final ResumeEducationRepository resumeEducationRepository;
    private final ResumeExperienceRepository resumeExperienceRepository;
    private final ResumeCertificationRepository resumeCertificationRepository;
    private final ResumeHistoryRepository resumeHistoryRepository;
    private final ResumeKeywordRepository resumeKeywordRepository;
    private final StudentJobRecommendationRepository recommendationRepository;
    private final StudentSkillGapRepository skillGapRepository;
    private final PlacementScoreRepository placementScoreRepository;
    private final LearningRoadmapRepository learningRoadmapRepository;
    private final RoadmapStepRepository roadmapStepRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final StudentDashboardMetricRepository studentDashboardMetricRepository;

    public JobRecommendationService(JobRepository jobRepository, JobSkillRepository jobSkillRepository, ParsedResumeRepository parsedResumeRepository, ResumeVersionRepository resumeVersionRepository, ResumeAnalysisRepository resumeAnalysisRepository, ResumeSkillRepository resumeSkillRepository, ResumeProjectRepository resumeProjectRepository, ResumeEducationRepository resumeEducationRepository, ResumeExperienceRepository resumeExperienceRepository, ResumeCertificationRepository resumeCertificationRepository, ResumeHistoryRepository resumeHistoryRepository, ResumeKeywordRepository resumeKeywordRepository, StudentJobRecommendationRepository recommendationRepository, StudentSkillGapRepository skillGapRepository, PlacementScoreRepository placementScoreRepository, LearningRoadmapRepository learningRoadmapRepository, RoadmapStepRepository roadmapStepRepository, JobApplicationRepository jobApplicationRepository, StudentDashboardMetricRepository studentDashboardMetricRepository) {
        this.jobRepository = jobRepository;
        this.jobSkillRepository = jobSkillRepository;
        this.parsedResumeRepository = parsedResumeRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.resumeSkillRepository = resumeSkillRepository;
        this.resumeProjectRepository = resumeProjectRepository;
        this.resumeEducationRepository = resumeEducationRepository;
        this.resumeExperienceRepository = resumeExperienceRepository;
        this.resumeCertificationRepository = resumeCertificationRepository;
        this.resumeHistoryRepository = resumeHistoryRepository;
        this.resumeKeywordRepository = resumeKeywordRepository;
        this.recommendationRepository = recommendationRepository;
        this.skillGapRepository = skillGapRepository;
        this.placementScoreRepository = placementScoreRepository;
        this.learningRoadmapRepository = learningRoadmapRepository;
        this.roadmapStepRepository = roadmapStepRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.studentDashboardMetricRepository = studentDashboardMetricRepository;
    }

    @Transactional
    public List<JobRecommendationResponse> refreshRecommendations(Long studentId) {
        ResumeSnapshot snapshot = loadLatestSnapshot(studentId);
        recommendationRepository.deleteByStudentIdAndResumeAnalysisId(studentId, snapshot.analysis().getId());

        List<Job> jobs = jobRepository.findByStatusOrderByCreatedAtDesc(com.skillforge.model.JobStatus.ACTIVE);
        List<StudentJobRecommendation> recommendations = new ArrayList<>();
        for (Job job : jobs) {
            RecommendationScoring scoring = scoreJob(snapshot, job);
            StudentJobRecommendation recommendation = new StudentJobRecommendation();
            recommendation.setStudentId(studentId);
            recommendation.setResumeAnalysisId(snapshot.analysis().getId());
            recommendation.setJob(job);
            recommendation.setMatchPercentage(scoring.matchPercentage());
            recommendation.setRecommendationScore(scoring.recommendationScore());
            recommendation.setRecommendationConfidence(scoring.confidence());
            recommendation.setReasonForRecommendation(scoring.reason());
            recommendation.setMatchingSkills(join(scoring.matchingSkills()));
            recommendation.setMissingRequirements(join(scoring.missingSkills()));
            recommendation.setStrengths(join(scoring.strengths()));
            recommendation.setWeaknesses(join(scoring.weaknesses()));
            recommendation.setExpectedSalaryMin(job.getSalaryMin());
            recommendation.setExpectedSalaryMax(job.getSalaryMax());
            recommendations.add(recommendation);
        }

        recommendationRepository.saveAll(recommendations);
        refreshSkillGaps(studentId, snapshot, recommendations);
        refreshPlacementScore(studentId, snapshot, recommendations);
        refreshRoadmaps(studentId, recommendations);
        refreshDashboardMetrics(studentId, snapshot, recommendations);

        return recommendationRepository.findByStudentIdAndResumeAnalysisIdOrderByMatchPercentageDesc(studentId, snapshot.analysis().getId())
                .stream()
                .map(JobRecommendationResponse::from)
                .toList();
    }

    public List<JobRecommendationResponse> getRecommendations(Long studentId) {
        ensureRecommendations(studentId);
        ResumeAnalysis analysis = resumeAnalysisRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume analysis not found"));
        return recommendationRepository.findByStudentIdAndResumeAnalysisIdOrderByMatchPercentageDesc(studentId, analysis.getId())
                .stream()
                .map(JobRecommendationResponse::from)
                .toList();
    }

    public JobRecommendationResponse getRecommendation(Long studentId, Long jobId) {
        ensureRecommendations(studentId);
        StudentJobRecommendation recommendation = recommendationRepository.findByStudentIdAndJobId(studentId, jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recommendation not found"));
        return JobRecommendationResponse.from(recommendation);
    }

    public List<SkillGapResponse> getSkillGaps(Long studentId) {
        ensureRecommendations(studentId);
        return skillGapRepository.findByStudentIdOrderByPriorityScoreDesc(studentId)
                .stream()
                .map(SkillGapResponse::from)
                .toList();
    }

    public SkillGapResponse getSkillGap(Long studentId, Long jobId) {
        ensureRecommendations(studentId);
        return skillGapRepository.findByStudentIdAndJobId(studentId, jobId)
                .map(SkillGapResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill gap not found"));
    }

    public PlacementScoreResponse getPlacementScore(Long studentId) {
        ensureRecommendations(studentId);
        return placementScoreRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId)
                .map(PlacementScoreResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Placement score not found"));
    }

    public List<LearningRoadmapResponse> getRoadmaps(Long studentId) {
        ensureRecommendations(studentId);
        List<LearningRoadmapResponse> responses = new ArrayList<>();
        for (LearningRoadmap roadmap : learningRoadmapRepository.findByStudentIdOrderByCreatedAtDesc(studentId)) {
            responses.add(LearningRoadmapResponse.from(roadmap, roadmapStepRepository.findByRoadmapIdOrderByStepOrderAsc(roadmap.getId())
                    .stream()
                    .map(LearningRoadmapStepResponse::from)
                    .toList()));
        }
        return responses;
    }

    public LearningRoadmapResponse getRoadmap(Long studentId, Long jobId) {
        ensureRecommendations(studentId);
        LearningRoadmap roadmap = learningRoadmapRepository.findByStudentIdAndJobId(studentId, jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning roadmap not found"));
        return LearningRoadmapResponse.from(roadmap, roadmapStepRepository.findByRoadmapIdOrderByStepOrderAsc(roadmap.getId())
                .stream()
                .map(LearningRoadmapStepResponse::from)
                .toList());
    }

    public StudentDashboardResponse getStudentDashboard(Long studentId) {
        ensureRecommendations(studentId);
        List<JobRecommendationResponse> recommendations = getRecommendations(studentId).stream().limit(5).toList();
        PlacementScoreResponse placementScore = getPlacementScore(studentId);
        SkillGapResponse topGap = getSkillGaps(studentId).stream().findFirst().orElse(null);
        StudentDashboardMetric metrics = studentDashboardMetricRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dashboard metrics not found"));

        StudentDashboardResponse response = new StudentDashboardResponse();
        response.setRecommendedJobs(recommendations);
        response.setPlacementScore(placementScore);
        response.setTopSkillGap(topGap);
        response.setAppliedJobsCount(jobApplicationRepository.countByStudentId(studentId));
        response.setResumeStatus(metrics.getResumeStatus());
        response.setAtsTrend(buildTrend(studentId));
        response.setPlacementTrend(buildPlacementTrend(studentId));
        response.setSkillsDistribution(buildSkillDistribution(studentId));
        response.setApplicationsByStatus(buildApplicationTrend(studentId));
        response.setRecentActivities(buildRecentActivities(studentId, recommendations));
        return response;
    }

    public List<com.skillforge.dto.JobGapRecommendationResponse> getJobInsights(Long studentId) {
        ensureRecommendations(studentId);
        List<com.skillforge.dto.JobGapRecommendationResponse> responses = new ArrayList<>();
        for (JobRecommendationResponse recommendation : getRecommendations(studentId).stream().limit(20).toList()) {
            com.skillforge.dto.JobGapRecommendationResponse item = new com.skillforge.dto.JobGapRecommendationResponse();
            item.setRecommendation(recommendation);
            item.setSkillGap(getSkillGap(studentId, recommendation.getJobId()));
            item.setRoadmap(getRoadmap(studentId, recommendation.getJobId()));
            responses.add(item);
        }
        return responses;
    }

    @Transactional
    public void seedAndRefresh(Long studentId) {
        refreshRecommendations(studentId);
    }

    private void refreshSkillGaps(Long studentId, ResumeSnapshot snapshot, List<StudentJobRecommendation> recommendations) {
        skillGapRepository.deleteAll(skillGapRepository.findByStudentIdOrderByPriorityScoreDesc(studentId));
        List<StudentSkillGap> gaps = new ArrayList<>();
        for (StudentJobRecommendation recommendation : recommendations) {
            SkillAnalysis analysis = analyzeSkillGap(snapshot, recommendation.getJob());
            StudentSkillGap gap = new StudentSkillGap();
            gap.setStudentId(studentId);
            gap.setJob(recommendation.getJob());
            gap.setRecommendation(recommendation);
            gap.setMatchedSkills(join(analysis.matchedSkills()));
            gap.setMissingSkills(join(analysis.missingSkills()));
            gap.setPartiallyMatchingSkills(join(analysis.partialSkills()));
            gap.setRecommendedSkills(join(analysis.recommendedSkills()));
            gap.setPriorityScore(analysis.priorityScore());
            gap.setDifficulty(analysis.difficulty());
            gap.setEstimatedLearningTimeHours(analysis.learningHours());
            gaps.add(gap);
        }
        skillGapRepository.saveAll(gaps);
    }

    private void refreshPlacementScore(Long studentId, ResumeSnapshot snapshot, List<StudentJobRecommendation> recommendations) {
        PlacementScore score = new PlacementScore();
        score.setStudentId(studentId);
        score.setResumeAnalysisId(snapshot.analysis().getId());
        score.setResumeScore(snapshot.analysis().getOverallScore());
        score.setProjectsScore(scoreProjects(snapshot));
        score.setCodingScore(scoreCoding(snapshot));
        score.setCommunicationScore(scoreCommunication(snapshot));
        score.setExperienceScore(scoreExperience(snapshot));
        score.setCertificationsScore(scoreCertifications(snapshot));
        score.setEducationScore(scoreEducation(snapshot));
        score.setRecommendationFitScore(recommendations.isEmpty() ? 0 : recommendations.get(0).getMatchPercentage());
        score.setOverallScore((score.getResumeScore() + score.getProjectsScore() + score.getCodingScore() + score.getCommunicationScore() + score.getExperienceScore() + score.getCertificationsScore() + score.getEducationScore() + score.getRecommendationFitScore()) / 8);
        score.setImprovementSuggestions(buildPlacementSuggestions(score, snapshot));
        placementScoreRepository.save(score);
    }

    private void refreshRoadmaps(Long studentId, List<StudentJobRecommendation> recommendations) {
        learningRoadmapRepository.deleteAll(learningRoadmapRepository.findByStudentIdOrderByCreatedAtDesc(studentId));
        List<LearningRoadmap> roadmaps = new ArrayList<>();
        for (StudentJobRecommendation recommendation : recommendations.stream().limit(10).toList()) {
            StudentSkillGap gap = skillGapRepository.findByStudentIdAndJobId(studentId, recommendation.getJob().getId())
                    .orElseThrow();
            LearningRoadmap roadmap = new LearningRoadmap();
            roadmap.setStudentId(studentId);
            roadmap.setJob(recommendation.getJob());
            roadmap.setRecommendation(recommendation);
            roadmap.setStatus(RoadmapStepStatus.PENDING);
            roadmap.setTotalSteps(0);
            roadmap.setCompletedSteps(0);
            roadmap.setInProgressSteps(0);
            roadmap.setPendingSteps(0);
            roadmaps.add(roadmap);
        }
        List<LearningRoadmap> savedRoadmaps = learningRoadmapRepository.saveAll(roadmaps);
        List<RoadmapStep> steps = new ArrayList<>();
        for (LearningRoadmap roadmap : savedRoadmaps) {
            StudentSkillGap gap = skillGapRepository.findByStudentIdAndJobId(studentId, roadmap.getJob().getId()).orElseThrow();
            List<String> missing = split(gap.getMissingSkills());
            int order = 1;
            for (String skill : missing) {
                RoadmapStep step = new RoadmapStep();
                step.setRoadmap(roadmap);
                step.setSkillName(skill);
                step.setLevel(roadmapLevel(skill));
                step.setResourceTitle(resourceTitle(skill, "Beginner"));
                step.setResourceUrl(resourceUrl(skill));
                step.setEstimatedDurationHours(durationHours(skill));
                step.setStatus(RoadmapStepStatus.PENDING);
                step.setStepOrder(order++);
                steps.add(step);
            }
            roadmap.setTotalSteps(steps.stream().filter(step -> step.getRoadmap().getId().equals(roadmap.getId())).toList().size());
            roadmap.setPendingSteps(roadmap.getTotalSteps());
            roadmap.setCompletedSteps(0);
            roadmap.setInProgressSteps(0);
        }
        roadmapStepRepository.saveAll(steps);
        learningRoadmapRepository.saveAll(savedRoadmaps);
    }

    private void refreshDashboardMetrics(Long studentId, ResumeSnapshot snapshot, List<StudentJobRecommendation> recommendations) {
        StudentDashboardMetric metric = studentDashboardMetricRepository.findByStudentId(studentId).orElse(new StudentDashboardMetric());
        metric.setStudentId(studentId);
        metric.setRecommendedJobsCount(recommendations.size());
        metric.setPlacementScore(placementScoreRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId).map(PlacementScore::getOverallScore).orElse(0));
        metric.setAtsScore(snapshot.analysis().getOverallScore());
        metric.setSkillGapCount(skillGapRepository.findByStudentIdOrderByPriorityScoreDesc(studentId).size());
        metric.setAppliedJobsCount((int) jobApplicationRepository.countByStudentId(studentId));
        metric.setResumeStatus(snapshot.resumeVersion().getStatus().name());
        studentDashboardMetricRepository.save(metric);
    }

    private void ensureRecommendations(Long studentId) {
        ResumeAnalysis analysis = resumeAnalysisRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume analysis not found"));
        if (!recommendationRepository.existsByStudentIdAndResumeAnalysisId(studentId, analysis.getId())) {
            refreshRecommendations(studentId);
        }
    }

    private ResumeSnapshot loadLatestSnapshot(Long studentId) {
        ResumeAnalysis analysis = resumeAnalysisRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume analysis not found"));
        ParsedResume parsedResume = parsedResumeRepository.findByResumeVersionId(analysis.getResumeVersionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parsed resume not found"));
        return new ResumeSnapshot(parsedResume, resumeVersionRepository.findById(analysis.getResumeVersionId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume version not found")), analysis);
    }

    private RecommendationScoring scoreJob(ResumeSnapshot snapshot, Job job) {
        Set<String> resumeSkills = extractResumeSkills(snapshot);
        List<String> jobRequiredSkills = jobSkillRepository.findByJobIdOrderByPriorityRankAsc(job.getId()).stream()
                .filter(skill -> skill.getSkillType() == JobSkillType.REQUIRED)
                .map(JobSkill::getSkillName)
                .toList();
        List<String> jobPreferredSkills = jobSkillRepository.findByJobIdOrderByPriorityRankAsc(job.getId()).stream()
                .filter(skill -> skill.getSkillType() == JobSkillType.PREFERRED)
                .map(JobSkill::getSkillName)
                .toList();

        List<String> matching = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String skill : jobRequiredSkills) {
            if (matches(resumeSkills, skill)) {
                matching.add(skill);
            } else {
                missing.add(skill);
            }
        }
        for (String skill : jobPreferredSkills) {
            if (matches(resumeSkills, skill)) {
                matching.add(skill);
            }
        }

        int ats = snapshot.analysis().getOverallScore();
        int experience = safeInt(snapshot.analysis().getExperienceCount()) * 10;
        int projects = safeInt(snapshot.analysis().getProjectCount()) * 8;
        int education = safeInt(snapshot.analysis().getEducationCount()) * 8;
        int certifications = safeInt(snapshot.analysis().getCertificationCount()) * 5;
        int keywords = safeInt(snapshot.analysis().getKeywordCoverage()) / 2;
        int cgpaScore = snapshot.parsedResume().getCgpa() == null ? 0 : snapshot.parsedResume().getCgpa().intValue() * 10;
        int base = (matching.size() * 10) - (missing.size() * 6) + ats / 2 + experience + projects + education + certifications + keywords + cgpaScore;
        int matchPercentage = clamp(base, 0, 100);
        int confidence = clamp(50 + matching.size() * 5 - missing.size() * 4 + Math.min(20, ats / 5), 0, 100);
        int recommendationScore = clamp((matchPercentage + confidence + Math.min(100, ats)) / 3, 0, 100);
        String reason = buildReason(job, matching, missing, snapshot);
        List<String> strengths = buildStrengths(snapshot, matching, job);
        List<String> weaknesses = buildWeaknesses(snapshot, missing, job);
        return new RecommendationScoring(matchPercentage, recommendationScore, confidence, reason, matching, missing, strengths, weaknesses);
    }

    private SkillAnalysis analyzeSkillGap(ResumeSnapshot snapshot, Job job) {
        Set<String> resumeSkills = extractResumeSkills(snapshot);
        List<JobSkill> jobSkills = jobSkillRepository.findByJobIdOrderByPriorityRankAsc(job.getId());
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> partial = new ArrayList<>();
        List<String> recommended = new ArrayList<>();

        for (JobSkill skill : jobSkills) {
            if (matches(resumeSkills, skill.getSkillName())) {
                matched.add(skill.getSkillName());
            } else if (containsPartial(resumeSkills, skill.getSkillName())) {
                partial.add(skill.getSkillName());
            } else {
                missing.add(skill.getSkillName());
                recommended.add(skill.getSkillName());
            }
        }

        int priority = clamp(missing.size() * 20 + partial.size() * 10 + (100 - snapshot.analysis().getOverallScore()) / 2, 0, 100);
        String difficulty = missing.size() > 6 ? "Hard" : missing.size() > 3 ? "Medium" : "Easy";
        int hours = missing.size() * 10 + partial.size() * 5 + 8;
        return new SkillAnalysis(matched, missing, partial, recommended, priority, difficulty, hours);
    }

    private String buildReason(Job job, List<String> matching, List<String> missing, ResumeSnapshot snapshot) {
        List<String> reasons = new ArrayList<>();
        reasons.add("Match score aligns with current ATS and parsed profile data.");
        if (!matching.isEmpty()) {
            reasons.add("Matching skills: " + String.join(", ", matching.stream().limit(4).toList()) + ".");
        }
        if (snapshot.analysis().getProjectCount() > 0) {
            reasons.add("Projects indicate practical delivery capability.");
        }
        if (job.getMinimumCgpa() == null || snapshot.parsedResume().getCgpa() == null || snapshot.parsedResume().getCgpa() >= job.getMinimumCgpa().doubleValue()) {
            reasons.add("CGPA requirement is satisfied or not specified.");
        }
        if (!missing.isEmpty()) {
            reasons.add("Missing requirements: " + String.join(", ", missing.stream().limit(4).toList()) + ".");
        }
        return String.join(" ", reasons);
    }

    private List<String> buildStrengths(ResumeSnapshot snapshot, List<String> matching, Job job) {
        List<String> strengths = new ArrayList<>();
        strengths.add("ATS score: " + snapshot.analysis().getOverallScore());
        strengths.add("Projects: " + snapshot.analysis().getProjectCount());
        strengths.add("Experience: " + snapshot.analysis().getExperienceCount());
        strengths.add("Matching skills: " + matching.size());
        if (snapshot.parsedResume().getCgpa() != null) {
            strengths.add("CGPA: " + snapshot.parsedResume().getCgpa());
        }
        if (job.getDeadline() != null) {
            strengths.add("Deadline available");
        }
        return strengths;
    }

    private List<String> buildWeaknesses(ResumeSnapshot snapshot, List<String> missing, Job job) {
        List<String> weaknesses = new ArrayList<>();
        if (snapshot.analysis().getExperienceCount() == 0) {
            weaknesses.add("No experience section detected");
        }
        if (snapshot.analysis().getCertificationCount() == 0) {
            weaknesses.add("No certifications detected");
        }
        if (missing.size() > 4) {
            weaknesses.add("Several required skills are missing");
        }
        if (job.getMinimumCgpa() != null && snapshot.parsedResume().getCgpa() != null && snapshot.parsedResume().getCgpa() < job.getMinimumCgpa().doubleValue()) {
            weaknesses.add("CGPA below requirement");
        }
        return weaknesses;
    }

    private Set<String> extractResumeSkills(ResumeSnapshot snapshot) {
        Set<String> skills = new LinkedHashSet<>();
        skills.addAll(resumeSkillRepository.findByParsedResumeIdOrderByIdAsc(snapshot.parsedResume().getId()).stream().map(item -> item.getSkillName().toLowerCase(Locale.ROOT)).collect(Collectors.toSet()));
        skills.addAll(resumeKeywordRepository.findByAnalysisIdOrderByPriorityRankAsc(snapshot.analysis().getId()).stream().map(item -> item.getKeywordText().toLowerCase(Locale.ROOT)).collect(Collectors.toSet()));
        return skills;
    }

    private boolean matches(Set<String> resumeSkills, String skill) {
        String normalized = skill.toLowerCase(Locale.ROOT);
        return resumeSkills.contains(normalized) || resumeSkills.stream().anyMatch(candidate -> candidate.contains(normalized) || normalized.contains(candidate));
    }

    private boolean containsPartial(Set<String> resumeSkills, String skill) {
        String normalized = skill.toLowerCase(Locale.ROOT);
        return resumeSkills.stream().anyMatch(candidate -> candidate.contains(normalized.split(" ")[0]) || normalized.contains(candidate.split(" ")[0]));
    }

    private List<StudentDashboardResponse.TrendPoint> buildTrend(Long studentId) {
        return resumeHistoryRepository.findByStudentIdOrderByVersionNumberDesc(studentId).stream().limit(6).map(item -> {
            StudentDashboardResponse.TrendPoint point = new StudentDashboardResponse.TrendPoint();
            point.setLabel("v" + item.getVersionNumber());
            point.setValue(item.getAtsScore());
            point.setColor("#0f4b8f");
            return point;
        }).toList();
    }

    private List<StudentDashboardResponse.TrendPoint> buildPlacementTrend(Long studentId) {
        return placementScoreRepository.findTop6ByStudentIdOrderByCreatedAtDesc(studentId).stream().map(item -> {
            StudentDashboardResponse.TrendPoint point = new StudentDashboardResponse.TrendPoint();
            point.setLabel(item.getCreatedAt().toLocalDate().toString());
            point.setValue(item.getOverallScore());
            point.setColor("#0e7490");
            return point;
        }).toList();
    }

    private List<StudentDashboardResponse.TrendPoint> buildSkillDistribution(Long studentId) {
        List<SkillGapResponse> gaps = getSkillGaps(studentId);
        int matched = gaps.stream().mapToInt(item -> item.getMatchedSkills().size()).sum();
        int missing = gaps.stream().mapToInt(item -> item.getMissingSkills().size()).sum();
        StudentDashboardResponse.TrendPoint matchedPoint = new StudentDashboardResponse.TrendPoint();
        matchedPoint.setLabel("Matched");
        matchedPoint.setValue(matched);
        matchedPoint.setColor("#16a34a");
        StudentDashboardResponse.TrendPoint missingPoint = new StudentDashboardResponse.TrendPoint();
        missingPoint.setLabel("Missing");
        missingPoint.setValue(missing);
        missingPoint.setColor("#dc2626");
        return List.of(matchedPoint, missingPoint);
    }

    private List<StudentDashboardResponse.TrendPoint> buildApplicationTrend(Long studentId) {
        return jobApplicationRepository.findByStudentIdOrderByAppliedAtDesc(studentId).stream()
                .collect(Collectors.groupingBy(application -> application.getStatus().name(), Collectors.counting()))
                .entrySet().stream().map(entry -> {
                    StudentDashboardResponse.TrendPoint point = new StudentDashboardResponse.TrendPoint();
                    point.setLabel(entry.getKey());
                    point.setValue(entry.getValue().intValue());
                    point.setColor("#0f4b8f");
                    return point;
                }).sorted(Comparator.comparing(StudentDashboardResponse.TrendPoint::getLabel)).toList();
    }

    private List<String> buildRecentActivities(Long studentId, List<JobRecommendationResponse> recommendations) {
        List<String> activities = new ArrayList<>();
        activities.add("Recommendations refreshed at " + LocalDateTime.now());
        if (!recommendations.isEmpty()) {
            activities.add("Top recommendation: " + recommendations.get(0).getJob().getTitle());
        }
        activities.add("Applied jobs: " + jobApplicationRepository.countByStudentId(studentId));
        activities.add("ATS score: " + resumeAnalysisRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId).map(ResumeAnalysis::getOverallScore).orElse(0));
        return activities;
    }

    private int scoreProjects(ResumeSnapshot snapshot) {
        return clamp(snapshot.analysis().getProjectCount() * 20, 0, 100);
    }

    private int scoreCoding(ResumeSnapshot snapshot) {
        int skills = resumeSkillRepository.findByParsedResumeIdOrderByIdAsc(snapshot.parsedResume().getId()).size();
        return clamp(skills * 8 + snapshot.analysis().getKeywordCoverage() / 2, 0, 100);
    }

    private int scoreCommunication(ResumeSnapshot snapshot) {
        int score = 20;
        if (snapshot.parsedResume().getLinkedinUrl() != null) score += 25;
        if (snapshot.parsedResume().getPortfolioUrl() != null) score += 15;
        if (snapshot.analysis().getReadabilityScore() != null) score += snapshot.analysis().getReadabilityScore() / 2;
        return clamp(score, 0, 100);
    }

    private int scoreExperience(ResumeSnapshot snapshot) {
        return clamp(snapshot.analysis().getExperienceCount() * 30, 0, 100);
    }

    private int scoreCertifications(ResumeSnapshot snapshot) {
        return clamp(snapshot.analysis().getCertificationCount() * 30, 0, 100);
    }

    private int scoreEducation(ResumeSnapshot snapshot) {
        return clamp(snapshot.analysis().getEducationCount() * 35 + (snapshot.parsedResume().getCgpa() == null ? 0 : 25), 0, 100);
    }

    private String buildPlacementSuggestions(PlacementScore score, ResumeSnapshot snapshot) {
        List<String> suggestions = new ArrayList<>();
        if (score.getResumeScore() < 70) suggestions.add("Improve resume completeness and ATS alignment");
        if (score.getProjectsScore() < 60) suggestions.add("Add stronger projects with measurable outcomes");
        if (score.getCodingScore() < 60) suggestions.add("Increase technical keyword coverage and coding depth");
        if (score.getCommunicationScore() < 60) suggestions.add("Add LinkedIn, portfolio, and concise summary");
        if (score.getExperienceScore() < 50) suggestions.add("Add internships, freelancing, or quantified work experience");
        if (score.getCertificationsScore() < 40) suggestions.add("Add relevant certifications");
        if (score.getEducationScore() < 50) suggestions.add("Improve education details and CGPA visibility");
        return suggestions.isEmpty() ? "Placement readiness is healthy; keep iterating on job-specific tailoring." : String.join("; ", suggestions);
    }

    private RoadmapSkillLevel roadmapLevel(String skill) {
        String lower = skill.toLowerCase(Locale.ROOT);
        if (lower.contains("java") || lower.contains("python") || lower.contains("sql") || lower.contains("communication")) {
            return RoadmapSkillLevel.BEGINNER;
        }
        if (lower.contains("spring") || lower.contains("react") || lower.contains("system design") || lower.contains("microservices")) {
            return RoadmapSkillLevel.INTERMEDIATE;
        }
        return RoadmapSkillLevel.ADVANCED;
    }

    private String resourceTitle(String skill, String stage) {
        return stage + " roadmap for " + skill;
    }

    private String resourceUrl(String skill) {
        String normalized = skill.toLowerCase(Locale.ROOT).replace(' ', '-');
        return "https://skillforge.ai/learn/" + normalized;
    }

    private int durationHours(String skill) {
        String lower = skill.toLowerCase(Locale.ROOT);
        if (lower.contains("communication") || lower.contains("resume")) return 6;
        if (lower.contains("java") || lower.contains("sql") || lower.contains("python")) return 12;
        return 16;
    }

    private String join(List<String> values) {
        return values == null || values.isEmpty() ? null : String.join(" | ", values);
    }

    private List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\s*\\|\\s*"));
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private int clamp(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }

    private record ResumeSnapshot(ParsedResume parsedResume, com.skillforge.model.ResumeVersion resumeVersion, ResumeAnalysis analysis) { }
    private record RecommendationScoring(int matchPercentage, int recommendationScore, int confidence, String reason, List<String> matchingSkills, List<String> missingSkills, List<String> strengths, List<String> weaknesses) { }
    private record SkillAnalysis(List<String> matchedSkills, List<String> missingSkills, List<String> partialSkills, List<String> recommendedSkills, int priorityScore, String difficulty, int learningHours) { }
}