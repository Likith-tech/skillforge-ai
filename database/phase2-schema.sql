-- SkillForge AI - Phase 2 schema reference
--
-- This is documentation, not a migration: the app runs with
-- spring.jpa.hibernate.ddl-auto=update, so Hibernate creates/updates these
-- tables automatically from the JPA entities in com.skillforge.model at
-- startup. Column names below match Spring Boot's default snake_case
-- naming strategy for the fields that don't have an explicit @Column name.
--
-- Phase 1 tables (users, roles, user_roles) already exist and are NOT
-- modified by Phase 2 - shown here only for FK context.

-- ============================================================
-- Phase 1 (existing, unchanged)
-- ============================================================
-- users(id, full_name, email, password, enabled, created_at, updated_at)
-- roles(id, name)                    -- STUDENT | RECRUITER | ADMIN
-- user_roles(user_id, role_id)       -- many-to-many

-- ============================================================
-- Phase 2
-- ============================================================

CREATE TABLE skills (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(80) NOT NULL UNIQUE,
    category VARCHAR(30) -- Module 2: PROGRAMMING_LANGUAGE | FRAMEWORK | DATABASE | CLOUD | DEVOPS | SOFT_SKILL | OTHER
);

-- Resume gains real file/ATS fields on top of the Phase 1 placeholder columns.
CREATE TABLE resumes (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id),
    title               VARCHAR(150) NOT NULL,
    content             TEXT,                 -- extracted plain text, used for ATS scoring
    original_file_name  VARCHAR(255) NOT NULL,
    stored_file_name    VARCHAR(255) NOT NULL, -- UUID-based name on disk under file.upload-dir/{user_id}/
    file_type           VARCHAR(10) NOT NULL,  -- PDF | DOCX
    file_size_bytes     BIGINT NOT NULL,
    ats_score           INTEGER,               -- 0-100, null until first upload
    deleted             BOOLEAN NOT NULL DEFAULT FALSE, -- soft delete (Module 1); row is kept so
                                                -- past applications' resume_id FK stays valid
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

-- Skills extracted from a resume's text (full catalog scan, not just the
-- baseline ATS checklist) - this is what job matching compares against.
CREATE TABLE resume_skills (
    resume_id BIGINT NOT NULL REFERENCES resumes(id),
    skill_id  BIGINT NOT NULL REFERENCES skills(id),
    PRIMARY KEY (resume_id, skill_id)
);

CREATE TABLE jobs (
    id               BIGSERIAL PRIMARY KEY,
    title            VARCHAR(150) NOT NULL,
    description      TEXT NOT NULL,
    company          VARCHAR(150) NOT NULL, -- free-text; kept for jobs posted before a Company profile existed
    company_id       BIGINT REFERENCES companies(id), -- Module 3: set when the recruiter has a Company profile
    location         VARCHAR(150),
    type             VARCHAR(20) NOT NULL,          -- FULL_TIME | INTERNSHIP | CONTRACT
    experience_level VARCHAR(10),                   -- Module 3: ENTRY | MID | SENIOR | LEAD, nullable
    salary_range     VARCHAR(100),                  -- Module 3: free text, e.g. "8-12 LPA"
    status           VARCHAR(10) NOT NULL DEFAULT 'OPEN', -- OPEN | CLOSED
    posted_by        BIGINT NOT NULL REFERENCES users(id), -- must hold RECRUITER role
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

CREATE TABLE job_skills (
    job_id   BIGINT NOT NULL REFERENCES jobs(id),
    skill_id BIGINT NOT NULL REFERENCES skills(id),
    PRIMARY KEY (job_id, skill_id)
);

-- One row per (job, student) application - the DB-level unique constraint
-- backs up the "already applied" check in ApplicationServiceImpl.
CREATE TABLE applications (
    id          BIGSERIAL PRIMARY KEY,
    job_id      BIGINT NOT NULL REFERENCES jobs(id),
    student_id  BIGINT NOT NULL REFERENCES users(id),
    resume_id   BIGINT NOT NULL REFERENCES resumes(id), -- resume used at time of applying
    match_score INTEGER NOT NULL,               -- skill-overlap % vs job.required_skills at apply time
    status      VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
    -- APPLIED | UNDER_REVIEW | SHORTLISTED | INTERVIEWING | REJECTED | HIRED
    -- (kept as-is from the original scaffold; Module 3's brief used slightly different
    -- wording for the same states - the frontend maps display labels, not the enum itself)
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    UNIQUE (job_id, student_id)
);

-- ============================================================
-- Module 3 - Job Portal & Recruitment System
-- ============================================================

-- One company profile per recruiter.
CREATE TABLE companies (
    id           BIGSERIAL PRIMARY KEY,
    owner_id     BIGINT NOT NULL UNIQUE REFERENCES users(id), -- must hold RECRUITER role
    company_name VARCHAR(150) NOT NULL,
    description  TEXT,
    website      VARCHAR(255),
    location     VARCHAR(150),
    logo         VARCHAR(500), -- URL to a hosted image, no upload subsystem for this
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL
);

CREATE TABLE saved_jobs (
    id         BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id),
    job_id     BIGINT NOT NULL REFERENCES jobs(id),
    saved_at   TIMESTAMP NOT NULL,
    UNIQUE (student_id, job_id)
);

-- ============================================================
-- Module 2 - AI ATS Intelligence Engine
-- ============================================================

-- Seeded reference data: which skills a target role expects, and how much
-- each one matters. DataSeeder populates this for all 7 TargetRole values.
CREATE TABLE role_skill_requirements (
    id       BIGSERIAL PRIMARY KEY,
    role     VARCHAR(30) NOT NULL, -- JAVA_DEVELOPER | PYTHON_DEVELOPER | FRONTEND_DEVELOPER |
                                    -- BACKEND_DEVELOPER | FULL_STACK_DEVELOPER | AI_ENGINEER | DATA_ANALYST
    skill_id BIGINT NOT NULL REFERENCES skills(id),
    priority VARCHAR(10) NOT NULL, -- HIGH | MEDIUM | LOW
    UNIQUE (role, skill_id)
);

-- One row per analysis run (append-only history; POST /ats/analyze always
-- inserts a new row rather than updating a prior one).
CREATE TABLE ats_analyses (
    id                   BIGSERIAL PRIMARY KEY,
    resume_id            BIGINT NOT NULL REFERENCES resumes(id),
    user_id              BIGINT NOT NULL REFERENCES users(id), -- denormalized from resume.user_id for history queries
    target_role          VARCHAR(30),           -- null = generic analysis, not compared to a specific role
    overall_score        INTEGER NOT NULL,      -- weighted composite of the six sub-scores below
    formatting_score     INTEGER NOT NULL,
    skills_score          INTEGER NOT NULL,
    education_score       INTEGER NOT NULL,
    experience_score      INTEGER NOT NULL,
    projects_score        INTEGER NOT NULL,
    certifications_score  INTEGER NOT NULL,
    extracted_name        VARCHAR(120),
    extracted_email       VARCHAR(150),
    extracted_phone       VARCHAR(30),
    created_at            TIMESTAMP NOT NULL
);

-- Collection tables Hibernate generates for AtsAnalysis's @ElementCollection fields.
CREATE TABLE ats_analysis_suggestions (
    analysis_id BIGINT NOT NULL REFERENCES ats_analyses(id),
    position    INTEGER NOT NULL,
    suggestion  VARCHAR(300)
);

CREATE TABLE ats_analysis_missing_skills (
    analysis_id BIGINT NOT NULL REFERENCES ats_analyses(id),
    skill_name  VARCHAR(80),
    priority    VARCHAR(10)
);

CREATE TABLE ats_analysis_skill_breakdown (
    analysis_id BIGINT NOT NULL REFERENCES ats_analyses(id),
    category    VARCHAR(30) NOT NULL,
    skill_count INTEGER,
    PRIMARY KEY (analysis_id, category)
);

-- ============================================================
-- Relationships
-- ============================================================
-- users            (1) --- (many) resumes           [resumes.user_id]
-- users            (1) --- (many) jobs               [jobs.posted_by, recruiter only]
-- users            (1) --- (many) applications        [applications.student_id]
-- resumes          (1) --- (many) applications        [applications.resume_id]
-- jobs             (1) --- (many) applications        [applications.job_id]
-- resumes          (many) --- (many) skills           [resume_skills]  = skills found in resume text
-- jobs             (many) --- (many) skills           [job_skills]     = skills a job requires
--
-- Job matching (JobMatchingServiceImpl) = overlap between a student's
-- latest resume's skill set and a job's required skill set:
--   matchScore = |resume.skills ∩ job.requiredSkills| / |job.requiredSkills| * 100
