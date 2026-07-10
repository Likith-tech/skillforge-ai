import ResumeAnalysisShell from "./ResumeAnalysisShell";
import { useResumeAnalysisDashboard } from "./useResumeAnalysisDashboard";
import "../ResumeAnalysis.css";

const renderKeywordGroup = (title, items, className = "") => (
  <article className="analysis-card">
    <h3>{title}</h3>
    <div className={`analysis-chip-cloud ${className}`}>
      {(items || []).map((item) => (
        <span className="analysis-chip" key={item.id || item.keywordText}>
          {item.keywordText}
        </span>
      ))}
    </div>
  </article>
);

function ResumeKeywordAnalysis() {
  const { data, loading, error } = useResumeAnalysisDashboard();

  if (loading) {
    return <main className="analysis-page">Loading keyword analysis...</main>;
  }

  const keywords = data?.keywords;

  return (
    <ResumeAnalysisShell title="Keyword Analysis" subtitle="Detected, missing, recommended, and important keywords derived from the actual uploaded resume.">
      {error && <div className="form-alert form-alert-error">{error}</div>}
      <section className="analysis-grid">
        {renderKeywordGroup("Detected Keywords", keywords?.detectedKeywords)}
        {renderKeywordGroup("Missing Keywords", keywords?.missingKeywords, "danger")}
        {renderKeywordGroup("Recommended Keywords", keywords?.recommendedKeywords, "secondary")}
        {renderKeywordGroup("Most Important Keywords", keywords?.importantKeywords)}
      </section>
    </ResumeAnalysisShell>
  );
}

export default ResumeKeywordAnalysis;