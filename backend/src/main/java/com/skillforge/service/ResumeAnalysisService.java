package com.skillforge.service;

import com.skillforge.dto.ResumeAnalysisDashboardResponse;
import com.skillforge.dto.ResumeAnalyticsResponse;
import com.skillforge.dto.ResumeComparisonResponse;
import com.skillforge.dto.ResumeHistoryEntryResponse;
import com.skillforge.dto.ResumeKeywordAnalysisResponse;
import com.skillforge.dto.ResumeParsedDataResponse;
import com.skillforge.dto.ResumeResponse;
import com.skillforge.dto.ResumeScoreBreakdownResponse;
import com.skillforge.dto.ResumeSuggestionResponse;
import com.skillforge.dto.ResumeVersionResponse;
import com.skillforge.model.ParsedResume;
import com.skillforge.model.Resume;
import com.skillforge.model.ResumeAnalysis;
import com.skillforge.model.ResumeAchievement;
import com.skillforge.model.ResumeCertification;
import com.skillforge.model.ResumeEducation;
import com.skillforge.model.ResumeExperience;
import com.skillforge.model.ResumeHistory;
import com.skillforge.model.ResumeKeyword;
import com.skillforge.model.ResumeLanguage;
import com.skillforge.model.ResumeProject;
import com.skillforge.model.ResumeSkill;
import com.skillforge.model.ResumeSuggestion;
import com.skillforge.model.ResumeStatus;
import com.skillforge.model.ResumeVersion;
import com.skillforge.repository.ParsedResumeRepository;
import com.skillforge.repository.ResumeAchievementRepository;
import com.skillforge.repository.ResumeAnalysisRepository;
import com.skillforge.repository.ResumeCertificationRepository;
import com.skillforge.repository.ResumeEducationRepository;
import com.skillforge.repository.ResumeExperienceRepository;
import com.skillforge.repository.ResumeHistoryRepository;
import com.skillforge.repository.ResumeKeywordRepository;
import com.skillforge.repository.ResumeLanguageRepository;
import com.skillforge.repository.ResumeProjectRepository;
import com.skillforge.repository.ResumeRepository;
import com.skillforge.repository.ResumeSkillRepository;
import com.skillforge.repository.ResumeSuggestionRepository;
import com.skillforge.repository.ResumeVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResumeAnalysisService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?:(?:\\+?\\d{1,3}[\\s-]?)?(?:\\(?\\d{3}\\)?[\\s-]?)?\\d{3}[\\s-]?\\d{4})");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s)]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern CGPA_PATTERN = Pattern.compile("(?i)(?:cgpa|gpa)[:\\s-]*([0-9]+(?:\\.[0-9]+)?)");
    private static final Pattern YEAR_PATTERN = Pattern.compile("(19|20)\\d{2}");
    private static final Pattern SECTION_PATTERN = Pattern.compile("^(education|academics|skills|technical skills|soft skills|experience|work experience|projects|certifications|certificates|languages|achievements|summary|objective)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Set<String> TECHNICAL_KEYWORDS = Set.of("java", "spring", "spring boot", "react", "javascript", "typescript", "python", "postgresql", "mysql", "mongodb", "docker", "aws", "rest", "graphql", "hibernate", "jpa", "git", "github", "sql", "html", "css", "nodejs", "express", "microservices", "linux");
    private static final Set<String> SOFT_KEYWORDS = Set.of("communication", "leadership", "teamwork", "problem solving", "adaptability", "collaboration", "critical thinking", "time management", "creativity", "initiative", "presentation");
    private static final Set<String> MISSING_BASE_KEYWORDS = Set.of("git", "github", "rest api", "spring boot", "javascript", "sql", "problem solving", "communication", "teamwork", "cloud");

    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeStorageService resumeStorageService;
    private final ParsedResumeRepository parsedResumeRepository;
    private final ResumeSkillRepository resumeSkillRepository;
    private final ResumeProjectRepository resumeProjectRepository;
    private final ResumeEducationRepository resumeEducationRepository;
    private final ResumeExperienceRepository resumeExperienceRepository;
    private final ResumeCertificationRepository resumeCertificationRepository;
    private final ResumeLanguageRepository resumeLanguageRepository;
    private final ResumeAchievementRepository resumeAchievementRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeKeywordRepository resumeKeywordRepository;
    private final ResumeSuggestionRepository resumeSuggestionRepository;
    private final ResumeHistoryRepository resumeHistoryRepository;

    public ResumeAnalysisService(
            ResumeRepository resumeRepository,
            ResumeVersionRepository resumeVersionRepository,
            ResumeStorageService resumeStorageService,
            ParsedResumeRepository parsedResumeRepository,
            ResumeSkillRepository resumeSkillRepository,
            ResumeProjectRepository resumeProjectRepository,
            ResumeEducationRepository resumeEducationRepository,
            ResumeExperienceRepository resumeExperienceRepository,
            ResumeCertificationRepository resumeCertificationRepository,
            ResumeLanguageRepository resumeLanguageRepository,
            ResumeAchievementRepository resumeAchievementRepository,
            ResumeAnalysisRepository resumeAnalysisRepository,
            ResumeKeywordRepository resumeKeywordRepository,
            ResumeSuggestionRepository resumeSuggestionRepository,
            ResumeHistoryRepository resumeHistoryRepository
    ) {
        this.resumeRepository = resumeRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.resumeStorageService = resumeStorageService;
        this.parsedResumeRepository = parsedResumeRepository;
        this.resumeSkillRepository = resumeSkillRepository;
        this.resumeProjectRepository = resumeProjectRepository;
        this.resumeEducationRepository = resumeEducationRepository;
        this.resumeExperienceRepository = resumeExperienceRepository;
        this.resumeCertificationRepository = resumeCertificationRepository;
        this.resumeLanguageRepository = resumeLanguageRepository;
        this.resumeAchievementRepository = resumeAchievementRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.resumeKeywordRepository = resumeKeywordRepository;
        this.resumeSuggestionRepository = resumeSuggestionRepository;
        this.resumeHistoryRepository = resumeHistoryRepository;
    }

    @Transactional
    public void analyzeAndPersist(Resume resume, ResumeVersion version) {
        if (resumeAnalysisRepository.findByResumeVersionId(version.getId()).isPresent()) {
            return;
        }

        String rawText = extractText(resume.getFilePath(), resume.getFileType());
        ParsedContent content = parseResume(rawText);

        ParsedResume parsedResume = new ParsedResume();
        parsedResume.setResumeId(resume.getId());
        parsedResume.setResumeVersionId(version.getId());
        parsedResume.setStudentId(resume.getStudentId());
        parsedResume.setFullName(content.fullName());
        parsedResume.setEmail(content.email());
        parsedResume.setPhoneNumber(content.phoneNumber());
        parsedResume.setAddress(content.address());
        parsedResume.setLinkedinUrl(content.linkedinUrl());
        parsedResume.setGithubUrl(content.githubUrl());
        parsedResume.setPortfolioUrl(content.portfolioUrl());
        parsedResume.setCgpa(content.cgpa());
        parsedResume.setSummaryText(content.summaryText());
        parsedResume.setRawText(rawText);
        ParsedResume savedParsedResume = parsedResumeRepository.save(parsedResume);

        saveSkills(savedParsedResume.getId(), content.skills());
        saveProjects(savedParsedResume.getId(), content.projects());
        saveEducation(savedParsedResume.getId(), content.education());
        saveExperience(savedParsedResume.getId(), content.experience());
        saveCertifications(savedParsedResume.getId(), content.certifications());
        saveLanguages(savedParsedResume.getId(), content.languages());
        saveAchievements(savedParsedResume.getId(), content.achievements());

        AnalysisOutcome outcome = scoreResume(content);

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setResumeId(resume.getId());
        analysis.setResumeVersionId(version.getId());
        analysis.setStudentId(resume.getStudentId());
        analysis.setOverallScore(outcome.overallScore());
        analysis.setStructureScore(outcome.structureScore());
        analysis.setFormattingScore(outcome.formattingScore());
        analysis.setSkillsScore(outcome.skillsScore());
        analysis.setEducationScore(outcome.educationScore());
        analysis.setProjectsScore(outcome.projectsScore());
        analysis.setExperienceScore(outcome.experienceScore());
        analysis.setKeywordsScore(outcome.keywordsScore());
        analysis.setReadabilityScore(outcome.readabilityScore());
        analysis.setContactInformationScore(outcome.contactInformationScore());
        analysis.setCompletenessScore(outcome.completenessScore());
        analysis.setSummaryText(outcome.summary());
        analysis.setDetectedSkillCount(content.skills().size());
        analysis.setTechnicalSkillCount((int) content.skills().stream().filter(item -> "TECHNICAL".equals(item.skillType())).count());
        analysis.setSoftSkillCount((int) content.skills().stream().filter(item -> "SOFT".equals(item.skillType())).count());
        analysis.setProjectCount(content.projects().size());
        analysis.setExperienceCount(content.experience().size());
        analysis.setEducationCount(content.education().size());
        analysis.setCertificationCount(content.certifications().size());
        analysis.setLanguageCount(content.languages().size());
        analysis.setKeywordCoverage(outcome.keywordCoverage());
        ResumeAnalysis savedAnalysis = resumeAnalysisRepository.save(analysis);

        saveKeywords(savedAnalysis.getId(), outcome);
        List<ResumeSuggestion> suggestions = saveSuggestions(savedAnalysis.getId(), content, outcome);

        Optional<ResumeHistory> previousHistory = resumeHistoryRepository.findByStudentIdOrderByVersionNumberDesc(resume.getStudentId())
                .stream()
                .filter(history -> !history.getResumeVersionId().equals(version.getId()))
                .findFirst();

        ResumeHistory history = new ResumeHistory();
        history.setResumeId(resume.getId());
        history.setResumeVersionId(version.getId());
        history.setStudentId(resume.getStudentId());
        history.setVersionNumber(version.getVersionNumber());
        history.setUploadedAt(version.getUploadedAt());
        history.setAtsScore(savedAnalysis.getOverallScore());
        history.setAnalyzedAt(LocalDateTime.now());
        previousHistory.ifPresent(item -> {
            history.setPreviousVersionId(item.getResumeVersionId());
            history.setPreviousAtsScore(item.getAtsScore());
            history.setChangesSummary(buildChangesSummary(item, savedAnalysis, content, suggestions));
        });
        if (history.getChangesSummary() == null) {
            history.setChangesSummary(buildInitialChangesSummary(content, suggestions));
        }
        resumeHistoryRepository.save(history);
    }

    public ResumeAnalysisDashboardResponse getDashboard(Long studentId) {
        Resume resume = findActiveResume(studentId);
        ResumeVersion version = findLatestVersion(studentId);
        ensureAnalyzed(resume, version);
        return buildDashboard(resume, version);
    }

    public ResumeParsedDataResponse getParsedResume(Long studentId) {
        Resume resume = findActiveResume(studentId);
        ResumeVersion version = findLatestVersion(studentId);
        ensureAnalyzed(resume, version);
        return buildParsedData(version.getId());
    }

    public ResumeScoreBreakdownResponse getScores(Long studentId) {
        ResumeAnalysis analysis = findLatestAnalysis(studentId);
        return ResumeScoreBreakdownResponse.from(analysis);
    }

    public ResumeKeywordAnalysisResponse getKeywords(Long studentId) {
        ResumeAnalysis analysis = findLatestAnalysis(studentId);
        return buildKeywordAnalysis(analysis.getId());
    }

    public List<ResumeSuggestionResponse> getSuggestions(Long studentId) {
        ResumeAnalysis analysis = findLatestAnalysis(studentId);
        return resumeSuggestionRepository.findByAnalysisIdOrderBySeverityRankAsc(analysis.getId())
                .stream()
                .map(ResumeSuggestionResponse::from)
                .toList();
    }

    public ResumeAnalyticsResponse getAnalytics(Long studentId) {
        Resume resume = findActiveResume(studentId);
        ResumeVersion version = findLatestVersion(studentId);
        ensureAnalyzed(resume, version);
        return buildAnalytics(studentId);
    }

    public List<ResumeHistoryEntryResponse> getHistory(Long studentId) {
        return resumeHistoryRepository.findByStudentIdOrderByVersionNumberDesc(studentId)
                .stream()
                .map(ResumeHistoryEntryResponse::from)
                .toList();
    }

    public ResumeComparisonResponse compare(Long studentId, Long leftVersionId, Long rightVersionId) {
        ResumeAnalysis left = resumeAnalysisRepository.findByResumeVersionId(leftVersionId)
                .filter(item -> item.getStudentId().equals(studentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Left resume version not found"));
        ResumeAnalysis right = resumeAnalysisRepository.findByResumeVersionId(rightVersionId)
                .filter(item -> item.getStudentId().equals(studentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Right resume version not found"));

        ParsedResume leftParsed = parsedResumeRepository.findByResumeVersionId(leftVersionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Left parsed resume not found"));
        ParsedResume rightParsed = parsedResumeRepository.findByResumeVersionId(rightVersionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Right parsed resume not found"));

        ResumeComparisonResponse response = new ResumeComparisonResponse();
        response.setLeftVersionId(leftVersionId);
        response.setRightVersionId(rightVersionId);
        response.setLeftScore(left.getOverallScore());
        response.setRightScore(right.getOverallScore());
        response.setScoreDelta(right.getOverallScore() - left.getOverallScore());
        response.setImprovements(compareImprovementFields(leftParsed, rightParsed));
        response.setRegressions(compareRegressionFields(leftParsed, rightParsed));
        response.setSharedHighlights(compareSharedHighlights(leftParsed, rightParsed));
        response.setChangedFields(compareChangedFields(leftParsed, rightParsed));
        return response;
    }

    public ResumeAnalysisDashboardResponse getAdminDashboard(Long studentId) {
        return getDashboard(studentId);
    }

    public ResumeParsedDataResponse getAdminParsedResume(Long studentId) {
        return getParsedResume(studentId);
    }

    public ResumeScoreBreakdownResponse getAdminScores(Long studentId) {
        return getScores(studentId);
    }

    public ResumeKeywordAnalysisResponse getAdminKeywords(Long studentId) {
        return getKeywords(studentId);
    }

    public List<ResumeSuggestionResponse> getAdminSuggestions(Long studentId) {
        return getSuggestions(studentId);
    }

    public ResumeAnalyticsResponse getAdminAnalytics(Long studentId) {
        return getAnalytics(studentId);
    }

    public List<ResumeHistoryEntryResponse> getAdminHistory(Long studentId) {
        return getHistory(studentId);
    }

    public ResumeComparisonResponse compareAdmin(Long studentId, Long leftVersionId, Long rightVersionId) {
        return compare(studentId, leftVersionId, rightVersionId);
    }

    private void ensureAnalyzed(Resume resume, ResumeVersion version) {
        if (resumeAnalysisRepository.findByResumeVersionId(version.getId()).isEmpty()) {
            analyzeAndPersist(resume, version);
        }
    }

    private Resume findActiveResume(Long studentId) {
        return resumeRepository.findByStudentIdAndStatus(studentId, ResumeStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active resume not found"));
    }

    private ResumeVersion findLatestVersion(Long studentId) {
        return resumeVersionRepository.findTopByStudentIdOrderByVersionNumberDesc(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume version not found"));
    }

    private ResumeAnalysis findLatestAnalysis(Long studentId) {
        return resumeAnalysisRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume analysis not found"));
    }

    private ResumeAnalysisDashboardResponse buildDashboard(Resume resume, ResumeVersion version) {
        ResumeAnalysis analysis = resumeAnalysisRepository.findByResumeVersionId(version.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume analysis not found"));
        ParsedResume parsedResume = parsedResumeRepository.findByResumeVersionId(version.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parsed resume not found"));

        ResumeAnalysisDashboardResponse response = new ResumeAnalysisDashboardResponse();
        response.setResume(ResumeResponse.from(resume));
        response.setResumeVersion(ResumeVersionResponse.from(version));
        response.setParsedResume(buildParsedData(version.getId()));
        response.setScores(ResumeScoreBreakdownResponse.from(analysis));
        response.setKeywords(buildKeywordAnalysis(analysis.getId()));
        response.setSuggestions(getSuggestions(resume.getStudentId()));
        response.setAnalytics(buildAnalytics(resume.getStudentId()));
        response.setHistory(getHistory(resume.getStudentId()));
        response.setLatestComparison(buildLatestComparison(resume.getStudentId(), parsedResume, analysis));
        return response;
    }

    private ResumeParsedDataResponse buildParsedData(Long resumeVersionId) {
        ParsedResume parsedResume = parsedResumeRepository.findByResumeVersionId(resumeVersionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parsed resume not found"));
        List<ResumeSkill> skills = resumeSkillRepository.findByParsedResumeIdOrderByIdAsc(parsedResume.getId());
        List<ResumeProject> projects = resumeProjectRepository.findByParsedResumeIdOrderByIdAsc(parsedResume.getId());
        List<ResumeEducation> education = resumeEducationRepository.findByParsedResumeIdOrderByIdAsc(parsedResume.getId());
        List<ResumeExperience> experience = resumeExperienceRepository.findByParsedResumeIdOrderByIdAsc(parsedResume.getId());
        List<ResumeCertification> certifications = resumeCertificationRepository.findByParsedResumeIdOrderByIdAsc(parsedResume.getId());
        List<ResumeLanguage> languages = resumeLanguageRepository.findByParsedResumeIdOrderByIdAsc(parsedResume.getId());
        List<ResumeAchievement> achievements = resumeAchievementRepository.findByParsedResumeIdOrderByIdAsc(parsedResume.getId());
        return ResumeParsedDataResponse.from(parsedResume, skills, projects, education, experience, certifications, languages, achievements);
    }

    private ResumeKeywordAnalysisResponse buildKeywordAnalysis(Long analysisId) {
        List<ResumeKeyword> keywords = resumeKeywordRepository.findByAnalysisIdOrderByPriorityRankAsc(analysisId);
        ResumeKeywordAnalysisResponse response = new ResumeKeywordAnalysisResponse();
        response.setDetectedKeywords(filterKeywordType(keywords, "DETECTED"));
        response.setMissingKeywords(filterKeywordType(keywords, "MISSING"));
        response.setRecommendedKeywords(filterKeywordType(keywords, "RECOMMENDED"));
        response.setImportantKeywords(filterKeywordType(keywords, "IMPORTANT"));
        return response;
    }

    private List<ResumeKeywordAnalysisResponse.KeywordItem> filterKeywordType(List<ResumeKeyword> keywords, String type) {
        return keywords.stream()
                .filter(item -> type.equals(item.getKeywordType()))
                .map(ResumeKeywordAnalysisResponse.KeywordItem::from)
                .toList();
    }

    private ResumeAnalyticsResponse buildAnalytics(Long studentId) {
        List<ResumeHistory> history = resumeHistoryRepository.findByStudentIdOrderByVersionNumberDesc(studentId);
        ResumeAnalysis latestAnalysis = findLatestAnalysis(studentId);

        ResumeAnalyticsResponse response = new ResumeAnalyticsResponse();
        response.setOverallAts(latestAnalysis.getOverallScore());
        response.setSkillsCount(latestAnalysis.getDetectedSkillCount());
        response.setProjectsCount(latestAnalysis.getProjectCount());
        response.setExperienceCount(latestAnalysis.getExperienceCount());
        response.setEducationCount(latestAnalysis.getEducationCount());
        response.setCertificationsCount(latestAnalysis.getCertificationCount());
        response.setLanguagesCount(latestAnalysis.getLanguageCount());
        response.setTechnicalSkillsCount(latestAnalysis.getTechnicalSkillCount());
        response.setSoftSkillsCount(latestAnalysis.getSoftSkillCount());
        response.setCompletenessScore(latestAnalysis.getCompletenessScore());
        response.setAtsTrend(history.stream().map(item -> {
            ResumeAnalyticsResponse.TrendPoint point = new ResumeAnalyticsResponse.TrendPoint();
            point.setLabel("v" + item.getVersionNumber());
            point.setValue(item.getAtsScore());
            return point;
        }).toList());

        List<ResumeAnalyticsResponse.DistributionPoint> skillDistribution = new ArrayList<>();
        skillDistribution.add(distribution("Technical", latestAnalysis.getTechnicalSkillCount(), "#2563eb"));
        skillDistribution.add(distribution("Soft", latestAnalysis.getSoftSkillCount(), "#14b8a6"));
        response.setSkillsDistribution(skillDistribution);

        List<ResumeAnalyticsResponse.DistributionPoint> completeness = new ArrayList<>();
        completeness.add(distribution("Contact", latestAnalysis.getContactInformationScore(), "#0f766e"));
        completeness.add(distribution("Structure", latestAnalysis.getStructureScore(), "#1d4ed8"));
        completeness.add(distribution("Formatting", latestAnalysis.getFormattingScore(), "#7c3aed"));
        completeness.add(distribution("Skills", latestAnalysis.getSkillsScore(), "#ea580c"));
        completeness.add(distribution("Education", latestAnalysis.getEducationScore(), "#be123c"));
        completeness.add(distribution("Projects", latestAnalysis.getProjectsScore(), "#0ea5e9"));
        completeness.add(distribution("Experience", latestAnalysis.getExperienceScore(), "#16a34a"));
        completeness.add(distribution("Readability", latestAnalysis.getReadabilityScore(), "#334155"));
        response.setCompletenessBreakdown(completeness);
        return response;
    }

    private ResumeAnalyticsResponse.DistributionPoint distribution(String label, Integer value, String color) {
        ResumeAnalyticsResponse.DistributionPoint point = new ResumeAnalyticsResponse.DistributionPoint();
        point.setLabel(label);
        point.setValue(value);
        point.setColor(color);
        return point;
    }

    private ResumeComparisonResponse buildLatestComparison(Long studentId, ParsedResume parsedResume, ResumeAnalysis analysis) {
        List<ResumeHistory> history = resumeHistoryRepository.findByStudentIdOrderByVersionNumberDesc(studentId);
        if (history.size() < 2) {
            ResumeComparisonResponse response = new ResumeComparisonResponse();
            response.setLeftVersionId(analysis.getResumeVersionId());
            response.setRightVersionId(analysis.getResumeVersionId());
            response.setLeftScore(analysis.getOverallScore());
            response.setRightScore(analysis.getOverallScore());
            response.setScoreDelta(0);
            response.setImprovements(List.of("This is the first analyzed version."));
            response.setRegressions(List.of());
            response.setSharedHighlights(List.of(parsedResume.getFullName() == null ? "Profile details available" : parsedResume.getFullName()));
            response.setChangedFields(List.of());
            return response;
        }

        ResumeHistory latest = history.get(0);
        ResumeHistory previous = history.get(1);
        ResumeComparisonResponse response = new ResumeComparisonResponse();
        response.setLeftVersionId(previous.getResumeVersionId());
        response.setRightVersionId(latest.getResumeVersionId());
        response.setLeftScore(previous.getAtsScore());
        response.setRightScore(latest.getAtsScore());
        response.setScoreDelta(latest.getAtsScore() - previous.getAtsScore());
        response.setImprovements(List.of(latest.getChangesSummary()));
        response.setRegressions(List.of());
        response.setSharedHighlights(List.of(parsedResume.getFullName() == null ? "Resume analyzed" : parsedResume.getFullName()));
        response.setChangedFields(List.of("ATS score", "keywords", "suggestions"));
        return response;
    }

    private void saveSkills(Long parsedResumeId, List<ParsedSkill> skills) {
        resumeSkillRepository.saveAll(skills.stream().map(item -> {
            ResumeSkill skill = new ResumeSkill();
            skill.setParsedResumeId(parsedResumeId);
            skill.setSkillName(item.skillName());
            skill.setSkillType(item.skillType());
            skill.setSourceSection(item.sourceSection());
            return skill;
        }).toList());
    }

    private void saveProjects(Long parsedResumeId, List<ParsedProject> projects) {
        resumeProjectRepository.saveAll(projects.stream().map(item -> {
            ResumeProject project = new ResumeProject();
            project.setParsedResumeId(parsedResumeId);
            project.setProjectName(item.projectName());
            project.setDescription(item.description());
            project.setTechnologies(item.technologies());
            project.setProjectUrl(item.projectUrl());
            return project;
        }).toList());
    }

    private void saveEducation(Long parsedResumeId, List<ParsedEducation> education) {
        resumeEducationRepository.saveAll(education.stream().map(item -> {
            ResumeEducation row = new ResumeEducation();
            row.setParsedResumeId(parsedResumeId);
            row.setInstitutionName(item.institutionName());
            row.setDegreeName(item.degreeName());
            row.setFieldOfStudy(item.fieldOfStudy());
            row.setStartYear(item.startYear());
            row.setEndYear(item.endYear());
            row.setCgpa(item.cgpa());
            row.setDescription(item.description());
            return row;
        }).toList());
    }

    private void saveExperience(Long parsedResumeId, List<ParsedExperience> experience) {
        resumeExperienceRepository.saveAll(experience.stream().map(item -> {
            ResumeExperience row = new ResumeExperience();
            row.setParsedResumeId(parsedResumeId);
            row.setCompanyName(item.companyName());
            row.setRoleTitle(item.roleTitle());
            row.setDescription(item.description());
            row.setStartDate(item.startDate());
            row.setEndDate(item.endDate());
            row.setCurrentRole(item.currentRole());
            return row;
        }).toList());
    }

    private void saveCertifications(Long parsedResumeId, List<ParsedCertification> certifications) {
        resumeCertificationRepository.saveAll(certifications.stream().map(item -> {
            ResumeCertification row = new ResumeCertification();
            row.setParsedResumeId(parsedResumeId);
            row.setCertificationName(item.certificationName());
            row.setIssuerName(item.issuerName());
            row.setIssueYear(item.issueYear());
            row.setDescription(item.description());
            return row;
        }).toList());
    }

    private void saveLanguages(Long parsedResumeId, List<ParsedLanguage> languages) {
        resumeLanguageRepository.saveAll(languages.stream().map(item -> {
            ResumeLanguage row = new ResumeLanguage();
            row.setParsedResumeId(parsedResumeId);
            row.setLanguageName(item.languageName());
            row.setProficiencyLevel(item.proficiencyLevel());
            return row;
        }).toList());
    }

    private void saveAchievements(Long parsedResumeId, List<ParsedAchievement> achievements) {
        resumeAchievementRepository.saveAll(achievements.stream().map(item -> {
            ResumeAchievement row = new ResumeAchievement();
            row.setParsedResumeId(parsedResumeId);
            row.setAchievementTitle(item.achievementTitle());
            row.setDescription(item.description());
            row.setAchievementYear(item.achievementYear());
            return row;
        }).toList());
    }

    private void saveKeywords(Long analysisId, AnalysisOutcome outcome) {
        List<ResumeKeyword> keywords = new ArrayList<>();
        int priority = 1;
        for (String keyword : outcome.detectedKeywords()) {
            keywords.add(createKeyword(analysisId, keyword, "DETECTED", priority++));
        }
        for (String keyword : outcome.missingKeywords()) {
            keywords.add(createKeyword(analysisId, keyword, "MISSING", priority++));
        }
        for (String keyword : outcome.recommendedKeywords()) {
            keywords.add(createKeyword(analysisId, keyword, "RECOMMENDED", priority++));
        }
        for (String keyword : outcome.importantKeywords()) {
            keywords.add(createKeyword(analysisId, keyword, "IMPORTANT", priority++));
        }
        resumeKeywordRepository.saveAll(keywords);
    }

    private ResumeKeyword createKeyword(Long analysisId, String keyword, String type, int priority) {
        ResumeKeyword item = new ResumeKeyword();
        item.setAnalysisId(analysisId);
        item.setKeywordText(keyword);
        item.setKeywordType(type);
        item.setPriorityRank(priority);
        return item;
    }

    private List<ResumeSuggestion> saveSuggestions(Long analysisId, ParsedContent content, AnalysisOutcome outcome) {
        List<SuggestionSeed> seeds = new ArrayList<>();
        if (content.phoneNumber() == null) {
            seeds.add(new SuggestionSeed("HIGH", "Contact Information", "Add a visible phone number to improve recruiter contactability.", 8, 1));
        }
        if (content.linkedinUrl() == null) {
            seeds.add(new SuggestionSeed("HIGH", "Contact Information", "Add a LinkedIn profile URL to strengthen professional credibility.", 7, 2));
        }
        if (content.githubUrl() == null) {
            seeds.add(new SuggestionSeed("HIGH", "Contact Information", "Add a GitHub profile URL to showcase code samples and projects.", 7, 3));
        }
        if (content.projects().size() < 2) {
            seeds.add(new SuggestionSeed("MEDIUM", "Projects", "Add at least two well-described projects with technologies and outcomes.", 10, 4));
        }
        if (content.certifications().isEmpty()) {
            seeds.add(new SuggestionSeed("MEDIUM", "Certifications", "Add relevant certifications to improve ATS credibility.", 5, 5));
        }
        if (content.achievements().isEmpty()) {
            seeds.add(new SuggestionSeed("LOW", "Achievements", "Add measurable achievements to demonstrate impact.", 4, 6));
        }
        if (content.skills().size() < 6) {
            seeds.add(new SuggestionSeed("HIGH", "Skills", "Expand the skills section with role-relevant technical and soft skills.", 12, 7));
        }
        if (content.summaryText() == null) {
            seeds.add(new SuggestionSeed("MEDIUM", "Summary", "Add a concise professional summary to improve readability and ATS fit.", 6, 8));
        }
        if (outcome.keywordCoverage() < 50) {
            seeds.add(new SuggestionSeed("MEDIUM", "Keywords", "Include more role-aligned keywords from the resume target domain.", 8, 9));
        }

        if (seeds.isEmpty()) {
            seeds.add(new SuggestionSeed("LOW", "Completeness", "Resume is well structured; keep tailoring keywords to specific job descriptions.", 3, 10));
        }

        return resumeSuggestionRepository.saveAll(seeds.stream().map(seed -> {
            ResumeSuggestion suggestion = new ResumeSuggestion();
            suggestion.setAnalysisId(analysisId);
            suggestion.setSeverity(seed.severity());
            suggestion.setCategory(seed.category());
            suggestion.setRecommendation(seed.recommendation());
            suggestion.setExpectedAtsImprovement(seed.expectedImprovement());
            suggestion.setSeverityRank(seed.severityRank());
            return suggestion;
        }).toList());
    }

    private String buildChangesSummary(ResumeHistory previous, ResumeAnalysis analysis, ParsedContent content, List<ResumeSuggestion> suggestions) {
        List<String> changes = new ArrayList<>();
        if (previous.getAtsScore() != null && !previous.getAtsScore().equals(analysis.getOverallScore())) {
            changes.add("ATS score changed from " + previous.getAtsScore() + " to " + analysis.getOverallScore());
        }
        changes.add("Skills detected: " + content.skills().size());
        changes.add("Projects detected: " + content.projects().size());
        changes.add("Suggestions generated: " + suggestions.size());
        return String.join("; ", changes);
    }

    private String buildInitialChangesSummary(ParsedContent content, List<ResumeSuggestion> suggestions) {
        return "Initial analysis created with " + content.skills().size() + " skills, " + content.projects().size() + " projects, and " + suggestions.size() + " suggestions.";
    }

    private List<String> compareImprovementFields(ParsedResume left, ParsedResume right) {
        List<String> improvements = new ArrayList<>();
        if (hasValue(right.getPhoneNumber()) && !hasValue(left.getPhoneNumber())) {
            improvements.add("Phone number added");
        }
        if (hasValue(right.getLinkedinUrl()) && !hasValue(left.getLinkedinUrl())) {
            improvements.add("LinkedIn profile added");
        }
        if (hasValue(right.getGithubUrl()) && !hasValue(left.getGithubUrl())) {
            improvements.add("GitHub profile added");
        }
        if (valueCount(right.getRawText()) > valueCount(left.getRawText())) {
            improvements.add("Content depth improved");
        }
        return improvements.isEmpty() ? List.of("No major improvements detected") : improvements;
    }

    private List<String> compareRegressionFields(ParsedResume left, ParsedResume right) {
        List<String> regressions = new ArrayList<>();
        if (!hasValue(right.getPhoneNumber()) && hasValue(left.getPhoneNumber())) {
            regressions.add("Phone number removed");
        }
        if (!hasValue(right.getLinkedinUrl()) && hasValue(left.getLinkedinUrl())) {
            regressions.add("LinkedIn profile removed");
        }
        if (!hasValue(right.getGithubUrl()) && hasValue(left.getGithubUrl())) {
            regressions.add("GitHub profile removed");
        }
        return regressions;
    }

    private List<String> compareSharedHighlights(ParsedResume left, ParsedResume right) {
        List<String> highlights = new ArrayList<>();
        if (hasValue(left.getEmail()) && hasValue(right.getEmail()) && left.getEmail().equalsIgnoreCase(right.getEmail())) {
            highlights.add("Email preserved");
        }
        if (hasValue(left.getFullName()) && hasValue(right.getFullName()) && left.getFullName().equalsIgnoreCase(right.getFullName())) {
            highlights.add("Name preserved");
        }
        if (left.getCgpa() != null && right.getCgpa() != null) {
            highlights.add("Education data retained");
        }
        return highlights.isEmpty() ? List.of("Common profile details retained") : highlights;
    }

    private List<String> compareChangedFields(ParsedResume left, ParsedResume right) {
        List<String> changed = new ArrayList<>();
        if (!equalsIgnoreCase(left.getSummaryText(), right.getSummaryText())) {
            changed.add("Summary");
        }
        if (!equalsIgnoreCase(left.getAddress(), right.getAddress())) {
            changed.add("Address");
        }
        if (!equalsIgnoreCase(left.getLinkedinUrl(), right.getLinkedinUrl())) {
            changed.add("LinkedIn");
        }
        if (!equalsIgnoreCase(left.getGithubUrl(), right.getGithubUrl())) {
            changed.add("GitHub");
        }
        if (!equalsIgnoreCase(left.getPortfolioUrl(), right.getPortfolioUrl())) {
            changed.add("Portfolio");
        }
        return changed.isEmpty() ? List.of("Structural changes only") : changed;
    }

    private String extractText(String filePath, String fileType) {
        Resource resource = resumeStorageService.loadAsResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            if ("application/pdf".equals(fileType)) {
                try (PDDocument document = PDDocument.load(inputStream)) {
                    return new PDFTextStripper().getText(document);
                }
            }
            if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(fileType)) {
                try (XWPFDocument document = new XWPFDocument(inputStream)) {
                    return document.getParagraphs().stream()
                            .map(paragraph -> paragraph.getText())
                            .filter(text -> text != null && !text.isBlank())
                            .collect(Collectors.joining("\n"));
                }
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to parse resume document");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported resume document type");
    }

    private ParsedContent parseResume(String rawText) {
        List<String> lines = rawText.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .toList();
        String normalizedText = String.join("\n", lines);

        String email = regexFindFirst(EMAIL_PATTERN, normalizedText);
        String phoneNumber = regexFindFirst(PHONE_PATTERN, normalizedText);
        String linkedinUrl = findUrlContaining(normalizedText, "linkedin.com");
        String githubUrl = findUrlContaining(normalizedText, "github.com");
        String portfolioUrl = findFirstPortfolioUrl(normalizedText);
        Double cgpa = parseCgpa(normalizedText);
        String fullName = inferFullName(lines, email, phoneNumber);
        String address = inferAddress(lines);
        String summaryText = extractSection(normalizedText, Set.of("summary", "objective"));

        SectionMap sectionMap = splitSections(lines);
        List<ParsedSkill> skills = parseSkills(sectionMap.getOrDefault("skills", List.of()), sectionMap.getOrDefault("technical skills", List.of()), sectionMap.getOrDefault("soft skills", List.of()));
        List<ParsedProject> projects = parseProjects(sectionMap.getOrDefault("projects", List.of()));
        List<ParsedEducation> education = parseEducation(sectionMap.getOrDefault("education", List.of()), normalizedText, cgpa);
        List<ParsedExperience> experience = parseExperience(sectionMap.getOrDefault("experience", List.of()), sectionMap.getOrDefault("work experience", List.of()));
        List<ParsedCertification> certifications = parseCertifications(sectionMap.getOrDefault("certifications", List.of()));
        List<ParsedLanguage> languages = parseLanguages(sectionMap.getOrDefault("languages", List.of()));
        List<ParsedAchievement> achievements = parseAchievements(sectionMap.getOrDefault("achievements", List.of()));

        if (skills.isEmpty()) {
            skills = extractSkillsFromText(normalizedText);
        }

        return new ParsedContent(fullName, email, phoneNumber, address, linkedinUrl, githubUrl, portfolioUrl, cgpa, summaryText,
                skills, projects, education, experience, certifications, languages, achievements, normalizedText);
    }

    private String regexFindFirst(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String findUrlContaining(String text, String fragment) {
        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            if (url.toLowerCase(Locale.ROOT).contains(fragment)) {
                return url;
            }
        }
        return null;
    }

    private String findFirstPortfolioUrl(String text) {
        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            String lower = url.toLowerCase(Locale.ROOT);
            if (!lower.contains("linkedin.com") && !lower.contains("github.com")) {
                return url;
            }
        }
        return null;
    }

    private Double parseCgpa(String text) {
        Matcher matcher = CGPA_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                return Double.valueOf(matcher.group(1));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String inferFullName(List<String> lines, String email, String phoneNumber) {
        for (String line : lines) {
            if (containsAny(line, email, phoneNumber)) {
                continue;
            }
            if (line.length() > 2 && line.length() < 80 && line.split("\\s+").length <= 5 && line.matches("[A-Za-z][A-Za-z\\s.'-]+")) {
                return line;
            }
        }
        return null;
    }

    private String inferAddress(List<String> lines) {
        for (String line : lines) {
            String lower = line.toLowerCase(Locale.ROOT);
            if (lower.contains("address") || lower.contains("street") || lower.contains("road") || lower.contains("city") || lower.contains("state") || lower.contains("pin")) {
                return line;
            }
        }
        return null;
    }

    private String extractSection(String text, Set<String> names) {
        List<String> lines = text.lines().map(String::trim).filter(line -> !line.isBlank()).toList();
        StringBuilder builder = new StringBuilder();
        boolean capturing = false;
        for (String line : lines) {
            String normalized = line.toLowerCase(Locale.ROOT);
            if (names.stream().anyMatch(normalized::equals)) {
                capturing = true;
                continue;
            }
            if (capturing && SECTION_PATTERN.matcher(normalized).matches()) {
                break;
            }
            if (capturing) {
                builder.append(line).append('\n');
            }
        }
        return builder.length() == 0 ? null : builder.toString().trim();
    }

    private SectionMap splitSections(List<String> lines) {
        Map<String, List<String>> sections = new LinkedHashMap<>();
        String currentSection = "header";
        sections.put(currentSection, new ArrayList<>());
        for (String line : lines) {
            String normalized = line.toLowerCase(Locale.ROOT).replace(":", "").trim();
            if (SECTION_PATTERN.matcher(normalized).matches()) {
                currentSection = normalized;
                sections.putIfAbsent(currentSection, new ArrayList<>());
                continue;
            }
            sections.computeIfAbsent(currentSection, key -> new ArrayList<>()).add(line);
        }
        return new SectionMap(sections);
    }

    private List<ParsedSkill> parseSkills(List<String> genericSkills, List<String> technicalSkills, List<String> softSkills) {
        List<ParsedSkill> results = new ArrayList<>();
        technicalSkills.forEach(line -> results.addAll(splitKeywords(line).stream().map(skill -> new ParsedSkill(skill, "TECHNICAL", "technical skills")).toList()));
        softSkills.forEach(line -> results.addAll(splitKeywords(line).stream().map(skill -> new ParsedSkill(skill, "SOFT", "soft skills")).toList()));
        genericSkills.forEach(line -> results.addAll(splitKeywords(line).stream().map(skill -> new ParsedSkill(skill, classifySkill(skill), "skills")).toList()));
        return uniqueSkills(results);
    }

    private List<ParsedSkill> extractSkillsFromText(String text) {
        Set<String> found = new LinkedHashSet<>();
        for (String keyword : TECHNICAL_KEYWORDS) {
            if (text.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) {
                found.add(keyword);
            }
        }
        for (String keyword : SOFT_KEYWORDS) {
            if (text.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) {
                found.add(keyword);
            }
        }
        return found.stream().map(skill -> new ParsedSkill(skill, classifySkill(skill), "detected")).toList();
    }

    private List<ParsedSkill> uniqueSkills(List<ParsedSkill> skills) {
        Map<String, ParsedSkill> deduped = new LinkedHashMap<>();
        for (ParsedSkill skill : skills) {
            String key = skill.skillName().toLowerCase(Locale.ROOT);
            deduped.putIfAbsent(key, skill);
        }
        return new ArrayList<>(deduped.values());
    }

    private List<String> splitKeywords(String text) {
        return Arrays.stream(text.split("[,;/•|-]"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .map(this::normalizeKeyword)
                .filter(item -> !item.isBlank())
                .toList();
    }

    private String normalizeKeyword(String keyword) {
        return keyword.replaceAll("\\s+", " ").trim();
    }

    private String classifySkill(String skill) {
        String lower = skill.toLowerCase(Locale.ROOT);
        if (SOFT_KEYWORDS.stream().anyMatch(lower::contains)) {
            return "SOFT";
        }
        return "TECHNICAL";
    }

    private List<ParsedProject> parseProjects(List<String> lines) {
        return parseBulletedSections(lines).stream()
                .map(item -> new ParsedProject(item.title(), item.description(), item.technologies(), item.url()))
                .toList();
    }

    private List<ParsedEducation> parseEducation(List<String> lines, String text, Double cgpa) {
        List<ParsedEducation> education = new ArrayList<>();
        if (!lines.isEmpty()) {
            String combined = String.join(" ", lines);
            Integer[] years = extractYears(combined);
            education.add(new ParsedEducation(extractInstitution(combined), extractDegree(combined), extractField(combined), years[0], years[1], cgpa, combined));
        }
        if (education.isEmpty() && cgpa != null) {
            education.add(new ParsedEducation(null, null, null, null, null, cgpa, "Education section inferred from resume text"));
        }
        return education;
    }

    private List<ParsedExperience> parseExperience(List<String> experienceLines, List<String> workExperienceLines) {
        List<String> lines = new ArrayList<>();
        lines.addAll(experienceLines);
        lines.addAll(workExperienceLines);
        if (lines.isEmpty()) {
            return List.of();
        }
        return parseBulletedSections(lines).stream()
                .map(item -> new ParsedExperience(item.companyName(), item.roleTitle(), item.description(), item.startDate(), item.endDate(), item.currentRole()))
                .toList();
    }

    private List<ParsedCertification> parseCertifications(List<String> lines) {
        return parseBulletedSections(lines).stream()
                .map(item -> new ParsedCertification(item.title(), item.issuer(), item.year(), item.description()))
                .toList();
    }

    private List<ParsedLanguage> parseLanguages(List<String> lines) {
        List<ParsedLanguage> result = new ArrayList<>();
        for (String line : lines) {
            for (String value : splitKeywords(line)) {
                result.add(new ParsedLanguage(value, inferProficiency(value)));
            }
        }
        return uniqueLanguages(result);
    }

    private List<ParsedAchievement> parseAchievements(List<String> lines) {
        return parseBulletedSections(lines).stream()
                .map(item -> new ParsedAchievement(item.title(), item.description(), item.year()))
                .toList();
    }

    private List<ParsedLanguage> uniqueLanguages(List<ParsedLanguage> languages) {
        Map<String, ParsedLanguage> deduped = new LinkedHashMap<>();
        for (ParsedLanguage language : languages) {
            deduped.putIfAbsent(language.languageName().toLowerCase(Locale.ROOT), language);
        }
        return new ArrayList<>(deduped.values());
    }

    private List<ParsedSectionItem> parseBulletedSections(List<String> lines) {
        List<ParsedSectionItem> items = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith("-") || line.startsWith("•") || line.startsWith("*")) {
                if (current.length() > 0) {
                    items.add(buildSectionItem(current.toString()));
                    current.setLength(0);
                }
                current.append(line.substring(1).trim());
            } else {
                if (current.length() > 0) {
                    current.append(' ');
                }
                current.append(line);
            }
        }
        if (current.length() > 0) {
            items.add(buildSectionItem(current.toString()));
        }
        return items;
    }

    private ParsedSectionItem buildSectionItem(String text) {
        String title = extractTitle(text);
        String url = regexFindFirst(URL_PATTERN, text);
        Integer year = extractYear(text);
        Integer[] years = extractYears(text);
        LocalDate startDate = years[0] == null ? null : LocalDate.of(years[0], 1, 1);
        LocalDate endDate = years[1] == null ? null : LocalDate.of(years[1], 12, 31);
        boolean currentRole = text.toLowerCase(Locale.ROOT).contains("present") || text.toLowerCase(Locale.ROOT).contains("current");
        return new ParsedSectionItem(
                title,
                text,
                inferTechnologies(text),
                url,
                inferIssuer(text),
                year,
                startDate,
                endDate,
                currentRole,
                inferCompanyName(text),
                title,
                url
        );
    }

    private String extractTitle(String text) {
        String firstSentence = text.split("[.:]", 2)[0];
        return firstSentence.length() > 120 ? firstSentence.substring(0, 120) : firstSentence.trim();
    }

    private String inferCompanyName(String text) {
        String[] parts = text.split(" at ");
        if (parts.length > 1) {
            return parts[1].split("[.,;-]", 2)[0].trim();
        }
        return null;
    }

    private String inferTechnologies(String text) {
        List<String> technologies = new ArrayList<>();
        String lower = text.toLowerCase(Locale.ROOT);
        for (String keyword : TECHNICAL_KEYWORDS) {
            if (lower.contains(keyword.toLowerCase(Locale.ROOT))) {
                technologies.add(keyword);
            }
        }
        return technologies.isEmpty() ? null : String.join(", ", technologies);
    }

    private String inferIssuer(String text) {
        String[] parts = text.split(" by ");
        if (parts.length > 1) {
            return parts[1].split("[.,;-]", 2)[0].trim();
        }
        return null;
    }

    private String inferProficiency(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        if (lower.contains("native") || lower.contains("fluent")) {
            return "Fluent";
        }
        if (lower.contains("intermediate")) {
            return "Intermediate";
        }
        if (lower.contains("basic") || lower.contains("beginner")) {
            return "Basic";
        }
        return "Proficient";
    }

    private Integer extractYear(String text) {
        Matcher matcher = YEAR_PATTERN.matcher(text);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group());
        }
        return null;
    }

    private Integer[] extractYears(String text) {
        Matcher matcher = YEAR_PATTERN.matcher(text);
        Integer startYear = null;
        Integer endYear = null;
        if (matcher.find()) {
            startYear = Integer.valueOf(matcher.group());
        }
        if (matcher.find()) {
            endYear = Integer.valueOf(matcher.group());
        }
        return new Integer[]{startYear, endYear};
    }

    private String extractDegree(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.contains("b.tech")) return "B.Tech";
        if (lower.contains("m.tech")) return "M.Tech";
        if (lower.contains("b.e")) return "B.E";
        if (lower.contains("mca")) return "MCA";
        if (lower.contains("b.sc")) return "B.Sc";
        if (lower.contains("m.sc")) return "M.Sc";
        if (lower.contains("bba")) return "BBA";
        if (lower.contains("mba")) return "MBA";
        return null;
    }

    private String extractInstitution(String text) {
        String[] parts = text.split("[;,|-]");
        for (String part : parts) {
            String lower = part.toLowerCase(Locale.ROOT);
            if (lower.contains("college") || lower.contains("university") || lower.contains("school")) {
                return part.trim();
            }
        }
        return null;
    }

    private String extractField(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.contains("computer science")) return "Computer Science";
        if (lower.contains("information technology")) return "Information Technology";
        if (lower.contains("electronics")) return "Electronics";
        if (lower.contains("mechanical")) return "Mechanical";
        return null;
    }

    private AnalysisOutcome scoreResume(ParsedContent content) {
        Set<String> detectedKeywords = detectKeywords(content.rawText());
        Set<String> missingKeywords = MISSING_BASE_KEYWORDS.stream()
                .filter(keyword -> !content.rawText().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> recommendedKeywords = recommendKeywords(content);
        Set<String> importantKeywords = importantKeywords(content);

        int structureScore = clamp(content.sectionCount() * 14 + (content.summaryText() != null ? 10 : 0), 0, 100);
        int formattingScore = clamp(100 - Math.max(0, countLongLines(content.rawText()) * 5) - Math.max(0, countBlankSections(content.rawText()) * 4), 0, 100);
        int skillsScore = clamp(content.skills().size() * 12 + (content.skills().stream().anyMatch(item -> "TECHNICAL".equals(item.skillType())) ? 20 : 0), 0, 100);
        int educationScore = clamp(content.education().size() * 35 + (content.cgpa() != null ? 30 : 0), 0, 100);
        int projectsScore = clamp(content.projects().size() * 35 + Math.min(30, countProjectTechnologies(content.projects()) * 5), 0, 100);
        int experienceScore = clamp(content.experience().size() * 40 + Math.min(20, content.experience().size() * 5), 0, 100);
        int keywordsScore = clamp((detectedKeywords.size() * 8) + Math.min(30, recommendedKeywords.size() * 4), 0, 100);
        int readabilityScore = clamp(100 - Math.min(40, averageLineLength(content.rawText()) / 3) - Math.min(20, content.rawText().split("\\n").length / 10), 0, 100);
        int contactScore = clamp((hasValue(content.email()) ? 20 : 0) + (hasValue(content.phoneNumber()) ? 20 : 0) + (hasValue(content.linkedinUrl()) ? 20 : 0) + (hasValue(content.githubUrl()) ? 20 : 0) + (hasValue(content.portfolioUrl()) ? 20 : 0), 0, 100);
        int completenessScore = clamp((contactScore + structureScore + skillsScore + educationScore + projectsScore + Math.min(100, content.experience().size() * 25)) / 6, 0, 100);
        int overallScore = clamp((int) Math.round((structureScore + formattingScore + skillsScore + educationScore + projectsScore + experienceScore + keywordsScore + readabilityScore + contactScore + completenessScore) / 10.0), 0, 100);

        List<String> detected = detectedKeywords.stream().sorted().toList();
        List<String> missing = missingKeywords.stream().toList();
        List<String> recommended = recommendedKeywords.stream().toList();
        List<String> important = importantKeywords.stream().toList();

        String summary = buildSummary(content, overallScore, contactScore, skillsScore, projectsScore, experienceScore);
        return new AnalysisOutcome(overallScore, structureScore, formattingScore, skillsScore, educationScore, projectsScore, experienceScore, keywordsScore,
                readabilityScore, contactScore, completenessScore, content.sectionCount(), detected, missing, recommended, important,
                Math.min(100, detectedKeywords.size() * 10 + recommendedKeywords.size() * 6), summary);
    }

    private String buildSummary(ParsedContent content, int overallScore, int contactScore, int skillsScore, int projectsScore, int experienceScore) {
        List<String> parts = new ArrayList<>();
        parts.add("ATS score is " + overallScore + ".");
        if (contactScore < 60) {
            parts.add("Strengthen contact details.");
        }
        if (skillsScore < 50) {
            parts.add("Expand the skills section.");
        }
        if (projectsScore < 50) {
            parts.add("Add deeper project evidence.");
        }
        if (experienceScore < 40) {
            parts.add("Add stronger experience bullets.");
        }
        if (content.summaryText() == null) {
            parts.add("Add a professional summary.");
        }
        return String.join(" ", parts);
    }

    private Set<String> detectKeywords(String text) {
        Set<String> detected = new LinkedHashSet<>();
        String lower = text.toLowerCase(Locale.ROOT);
        for (String keyword : TECHNICAL_KEYWORDS) {
            if (lower.contains(keyword.toLowerCase(Locale.ROOT))) {
                detected.add(keyword);
            }
        }
        for (String keyword : SOFT_KEYWORDS) {
            if (lower.contains(keyword.toLowerCase(Locale.ROOT))) {
                detected.add(keyword);
            }
        }
        return detected;
    }

    private Set<String> recommendKeywords(ParsedContent content) {
        Set<String> recommended = new LinkedHashSet<>();
        if (content.skills().stream().noneMatch(item -> "TECHNICAL".equals(item.skillType()))) {
            recommended.add("technical skills");
        }
        if (content.projects().isEmpty()) {
            recommended.add("project delivery");
        }
        if (content.experience().isEmpty()) {
            recommended.add("internship experience");
        }
        if (content.education().isEmpty()) {
            recommended.add("academic background");
        }
        recommended.addAll(TECHNICAL_KEYWORDS.stream().limit(6).collect(Collectors.toCollection(LinkedHashSet::new)));
        return recommended;
    }

    private Set<String> importantKeywords(ParsedContent content) {
        Set<String> important = new LinkedHashSet<>();
        important.add("resume");
        important.add("ats");
        if (content.linkedinUrl() != null) important.add("linkedin");
        if (content.githubUrl() != null) important.add("github");
        if (!content.projects().isEmpty()) important.add("project");
        if (!content.experience().isEmpty()) important.add("experience");
        return important;
    }

    private int countLongLines(String text) {
        return (int) text.lines().filter(line -> line.length() > 160).count();
    }

    private int countBlankSections(String text) {
        return (int) Arrays.stream(text.split("\\n\\n+")).filter(String::isBlank).count();
    }

    private int countProjectTechnologies(List<ParsedProject> projects) {
        return (int) projects.stream().filter(project -> project.technologies() != null && !project.technologies().isBlank()).count();
    }

    private int averageLineLength(String text) {
        List<String> lines = text.lines().filter(line -> !line.isBlank()).toList();
        if (lines.isEmpty()) {
            return 0;
        }
        int total = lines.stream().mapToInt(String::length).sum();
        return total / lines.size();
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    private boolean equalsIgnoreCase(String left, String right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return left.trim().equalsIgnoreCase(right.trim());
    }

    private boolean containsAny(String line, String... values) {
        if (line == null) {
            return false;
        }
        for (String value : values) {
            if (value != null && line.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private int valueCount(String value) {
        return value == null ? 0 : value.length();
    }

    private int clamp(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }

    private record ParsedContent(
            String fullName,
            String email,
            String phoneNumber,
            String address,
            String linkedinUrl,
            String githubUrl,
            String portfolioUrl,
            Double cgpa,
            String summaryText,
            List<ParsedSkill> skills,
            List<ParsedProject> projects,
            List<ParsedEducation> education,
            List<ParsedExperience> experience,
            List<ParsedCertification> certifications,
            List<ParsedLanguage> languages,
            List<ParsedAchievement> achievements,
            String rawText
    ) {
        int sectionCount() {
            int count = 0;
            if (summaryText != null) count++;
            if (!skills.isEmpty()) count++;
            if (!projects.isEmpty()) count++;
            if (!education.isEmpty()) count++;
            if (!experience.isEmpty()) count++;
            if (!certifications.isEmpty()) count++;
            if (!languages.isEmpty()) count++;
            if (!achievements.isEmpty()) count++;
            return count;
        }
    }

    private record ParsedSkill(String skillName, String skillType, String sourceSection) { }

    private record ParsedProject(String projectName, String description, String technologies, String projectUrl) { }

    private record ParsedEducation(String institutionName, String degreeName, String fieldOfStudy, Integer startYear, Integer endYear, Double cgpa, String description) { }

    private record ParsedExperience(String companyName, String roleTitle, String description, LocalDate startDate, LocalDate endDate, boolean currentRole) { }

    private record ParsedCertification(String certificationName, String issuerName, Integer issueYear, String description) { }

    private record ParsedLanguage(String languageName, String proficiencyLevel) { }

    private record ParsedAchievement(String achievementTitle, String description, Integer achievementYear) { }

    private record ParsedSectionItem(String title, String description, String technologies, String url, String issuer, Integer year, LocalDate startDate, LocalDate endDate, boolean currentRole, String companyName, String roleTitle, String projectUrl) { }

    private record SectionMap(Map<String, List<String>> sections) {
        List<String> getOrDefault(String key, List<String> fallback) {
            return sections.getOrDefault(key, fallback);
        }
    }

    private record AnalysisOutcome(
            int overallScore,
            int structureScore,
            int formattingScore,
            int skillsScore,
            int educationScore,
            int projectsScore,
            int experienceScore,
            int keywordsScore,
            int readabilityScore,
            int contactInformationScore,
            int completenessScore,
            int sectionCount,
            List<String> detectedKeywords,
            List<String> missingKeywords,
            List<String> recommendedKeywords,
            List<String> importantKeywords,
            int keywordCoverage,
            String summary
    ) { }

    private record SuggestionSeed(String severity, String category, String recommendation, int expectedImprovement, int severityRank) { }
}