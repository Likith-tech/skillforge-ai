package com.skillforge.dto;

import com.skillforge.model.ParsedResume;
import com.skillforge.model.ResumeAchievement;
import com.skillforge.model.ResumeCertification;
import com.skillforge.model.ResumeEducation;
import com.skillforge.model.ResumeExperience;
import com.skillforge.model.ResumeLanguage;
import com.skillforge.model.ResumeProject;
import com.skillforge.model.ResumeSkill;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeParsedDataResponse {

    private Long parsedResumeId;
    private Long resumeId;
    private Long resumeVersionId;
    private Long studentId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private Double cgpa;
    private String summaryText;
    private String rawText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SkillItem> skills;
    private List<ProjectItem> projects;
    private List<EducationItem> education;
    private List<ExperienceItem> experience;
    private List<CertificationItem> certifications;
    private List<LanguageItem> languages;
    private List<AchievementItem> achievements;

    public static ResumeParsedDataResponse from(
            ParsedResume parsedResume,
            List<ResumeSkill> skills,
            List<ResumeProject> projects,
            List<ResumeEducation> education,
            List<ResumeExperience> experience,
            List<ResumeCertification> certifications,
            List<ResumeLanguage> languages,
            List<ResumeAchievement> achievements
    ) {
        ResumeParsedDataResponse response = new ResumeParsedDataResponse();
        response.setParsedResumeId(parsedResume.getId());
        response.setResumeId(parsedResume.getResumeId());
        response.setResumeVersionId(parsedResume.getResumeVersionId());
        response.setStudentId(parsedResume.getStudentId());
        response.setFullName(parsedResume.getFullName());
        response.setEmail(parsedResume.getEmail());
        response.setPhoneNumber(parsedResume.getPhoneNumber());
        response.setAddress(parsedResume.getAddress());
        response.setLinkedinUrl(parsedResume.getLinkedinUrl());
        response.setGithubUrl(parsedResume.getGithubUrl());
        response.setPortfolioUrl(parsedResume.getPortfolioUrl());
        response.setCgpa(parsedResume.getCgpa());
        response.setSummaryText(parsedResume.getSummaryText());
        response.setRawText(parsedResume.getRawText());
        response.setCreatedAt(parsedResume.getCreatedAt());
        response.setUpdatedAt(parsedResume.getUpdatedAt());
        response.setSkills(skills.stream().map(SkillItem::from).toList());
        response.setProjects(projects.stream().map(ProjectItem::from).toList());
        response.setEducation(education.stream().map(EducationItem::from).toList());
        response.setExperience(experience.stream().map(ExperienceItem::from).toList());
        response.setCertifications(certifications.stream().map(CertificationItem::from).toList());
        response.setLanguages(languages.stream().map(LanguageItem::from).toList());
        response.setAchievements(achievements.stream().map(AchievementItem::from).toList());
        return response;
    }

    @Getter
    @Setter
    public static class SkillItem {
        private Long id;
        private String skillName;
        private String skillType;
        private String sourceSection;

        public static SkillItem from(ResumeSkill skill) {
            SkillItem item = new SkillItem();
            item.setId(skill.getId());
            item.setSkillName(skill.getSkillName());
            item.setSkillType(skill.getSkillType());
            item.setSourceSection(skill.getSourceSection());
            return item;
        }
    }

    @Getter
    @Setter
    public static class ProjectItem {
        private Long id;
        private String projectName;
        private String description;
        private String technologies;
        private String projectUrl;

        public static ProjectItem from(ResumeProject project) {
            ProjectItem item = new ProjectItem();
            item.setId(project.getId());
            item.setProjectName(project.getProjectName());
            item.setDescription(project.getDescription());
            item.setTechnologies(project.getTechnologies());
            item.setProjectUrl(project.getProjectUrl());
            return item;
        }
    }

    @Getter
    @Setter
    public static class EducationItem {
        private Long id;
        private String institutionName;
        private String degreeName;
        private String fieldOfStudy;
        private Integer startYear;
        private Integer endYear;
        private Double cgpa;
        private String description;

        public static EducationItem from(ResumeEducation education) {
            EducationItem item = new EducationItem();
            item.setId(education.getId());
            item.setInstitutionName(education.getInstitutionName());
            item.setDegreeName(education.getDegreeName());
            item.setFieldOfStudy(education.getFieldOfStudy());
            item.setStartYear(education.getStartYear());
            item.setEndYear(education.getEndYear());
            item.setCgpa(education.getCgpa());
            item.setDescription(education.getDescription());
            return item;
        }
    }

    @Getter
    @Setter
    public static class ExperienceItem {
        private Long id;
        private String companyName;
        private String roleTitle;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean currentRole;

        public static ExperienceItem from(ResumeExperience experience) {
            ExperienceItem item = new ExperienceItem();
            item.setId(experience.getId());
            item.setCompanyName(experience.getCompanyName());
            item.setRoleTitle(experience.getRoleTitle());
            item.setDescription(experience.getDescription());
            item.setStartDate(experience.getStartDate());
            item.setEndDate(experience.getEndDate());
            item.setCurrentRole(experience.isCurrentRole());
            return item;
        }
    }

    @Getter
    @Setter
    public static class CertificationItem {
        private Long id;
        private String certificationName;
        private String issuerName;
        private Integer issueYear;
        private String description;

        public static CertificationItem from(ResumeCertification certification) {
            CertificationItem item = new CertificationItem();
            item.setId(certification.getId());
            item.setCertificationName(certification.getCertificationName());
            item.setIssuerName(certification.getIssuerName());
            item.setIssueYear(certification.getIssueYear());
            item.setDescription(certification.getDescription());
            return item;
        }
    }

    @Getter
    @Setter
    public static class LanguageItem {
        private Long id;
        private String languageName;
        private String proficiencyLevel;

        public static LanguageItem from(ResumeLanguage language) {
            LanguageItem item = new LanguageItem();
            item.setId(language.getId());
            item.setLanguageName(language.getLanguageName());
            item.setProficiencyLevel(language.getProficiencyLevel());
            return item;
        }
    }

    @Getter
    @Setter
    public static class AchievementItem {
        private Long id;
        private String achievementTitle;
        private String description;
        private Integer achievementYear;

        public static AchievementItem from(ResumeAchievement achievement) {
            AchievementItem item = new AchievementItem();
            item.setId(achievement.getId());
            item.setAchievementTitle(achievement.getAchievementTitle());
            item.setDescription(achievement.getDescription());
            item.setAchievementYear(achievement.getAchievementYear());
            return item;
        }
    }
}