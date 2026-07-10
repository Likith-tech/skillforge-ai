import { Link } from "react-router-dom";
import ResumeAnalysisShell from "./ResumeAnalysisShell";
import { useResumeAnalysisDashboard } from "./useResumeAnalysisDashboard";
import "../ResumeAnalysis.css";

const scoreTone = (score) => {
  if (score >= 80) return "success";
  if (score >= 60) return "warning";
  return "danger";
};

const formatPercent = (value) => `${Math.max(0, Math.min(100, value || 0))}%`;

function ResumeAnalysisDashboard() {
  const { data, loading, error } = useResumeAnalysisDashboard();

  if (loading) {
    return <main className="analysis-page">Loading analysis dashboard...</main>;
  }

  if (error) {
    return (
      <ResumeAnalysisShell title="Analysis Dashboard" subtitle="Production ATS analysis for your latest uploaded resume.">
        <div className="form-alert form-alert-error">{error}</div>
      </ResumeAnalysisShell>
    );
  }

  const { resume, parsedResume, scores, keywords, suggestions, analytics, history, latestComparison } = data;

  return (
    <ResumeAnalysisShell
      title="Analysis Dashboard"
      subtitle="Production ATS scoring, keyword intelligence, and profile quality tracking from the latest uploaded resume."
      actions={
        <>
          <Link className="analysis-button secondary" to="/student/resume/upload">
            Replace Resume
          </Link>
          <Link className="analysis-button" to="/student/resume/preview">
            Preview Resume
          </Link>
        </>
      }
    >
      <section className="analysis-hero">
        <div className={`analysis-score-ring ${scoreTone(scores?.overallScore)}`}>
          <div>
            <span className="analysis-score-label">Overall ATS</span>
            <strong>{scores?.overallScore ?? 0}</strong>
            <span>/100</span>
          </div>
        </div>

        <div className="analysis-hero-copy">
          <h2>{parsedResume?.fullName || resume?.originalFileName || "Resume analysis ready"}</h2>
          <p>{parsedResume?.summaryText || "Every upload is parsed, scored, and stored in PostgreSQL automatically."}</p>

          <div className="analysis-mini-grid">
            <article className="analysis-mini-card">
              <span>Skills</span>
              <strong>{analytics?.skillsCount ?? 0}</strong>
            </article>
            <article className="analysis-mini-card">
              <span>Projects</span>
              <strong>{analytics?.projectsCount ?? 0}</strong>
            </article>
            <article className="analysis-mini-card">
              <span>Experience</span>
              <strong>{analytics?.experienceCount ?? 0}</strong>
            </article>
            <article className="analysis-mini-card">
              <span>Education</span>
              <strong>{analytics?.educationCount ?? 0}</strong>
            </article>
          </div>
        </div>
      </section>

      <section className="analysis-grid">
        <article className="analysis-card">
          <h3>Contact Information</h3>
          <p>{parsedResume?.email || "Email missing"}</p>
          <p>{parsedResume?.phoneNumber || "Phone missing"}</p>
          <p>{parsedResume?.linkedinUrl || "LinkedIn missing"}</p>
          <p>{parsedResume?.githubUrl || "GitHub missing"}</p>
        </article>
        <article className="analysis-card">
          <h3>Top Suggestions</h3>
          <ul className="analysis-list">
            {(suggestions || []).slice(0, 4).map((item) => (
              <li key={item.id}>
                <strong>{item.severity}</strong>
                <span>{item.recommendation}</span>
              </li>
            ))}
          </ul>
        </article>
        <article className="analysis-card">
          <h3>Latest Comparison</h3>
          <p>{latestComparison?.scoreDelta >= 0 ? `+${latestComparison?.scoreDelta}` : latestComparison?.scoreDelta} ATS change</p>
          <p>{(latestComparison?.changedFields || []).join(", ") || "No comparison data yet"}</p>
        </article>
      </section>

      <section className="analysis-panel">
        <div className="analysis-panel-header">
          <h3>Score Breakdown</h3>
          <Link to="/student/resume/analysis/ats">Open ATS report</Link>
        </div>
        <div className="analysis-score-list">
          {[
            ["Structure", scores?.structureScore],
            ["Formatting", scores?.formattingScore],
            ["Skills", scores?.skillsScore],
            ["Education", scores?.educationScore],
            ["Projects", scores?.projectsScore],
            ["Experience", scores?.experienceScore],
            ["Keywords", scores?.keywordsScore],
            ["Readability", scores?.readabilityScore],
            ["Contact", scores?.contactInformationScore],
            ["Completeness", scores?.completenessScore],
          ].map(([label, value]) => (
            <div key={label} className="analysis-score-row">
              <div className="analysis-score-row-label">
                <span>{label}</span>
                <strong>{value ?? 0}</strong>
              </div>
              <div className="analysis-progress">
                <span style={{ width: formatPercent(value) }} />
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="analysis-grid analysis-grid-tight">
        <article className="analysis-card">
          <h3>Detected Keywords</h3>
          <div className="analysis-chip-cloud">
            {(keywords?.detectedKeywords || []).map((item) => (
              <span className="analysis-chip" key={item.id}>{item.keywordText}</span>
            ))}
          </div>
        </article>
        <article className="analysis-card">
          <h3>Recommended Keywords</h3>
          <div className="analysis-chip-cloud">
            {(keywords?.recommendedKeywords || []).map((item) => (
              <span className="analysis-chip secondary" key={item.id}>{item.keywordText}</span>
            ))}
          </div>
        </article>
      </section>
    </ResumeAnalysisShell>
  );
}

export default ResumeAnalysisDashboard;