import ResumeAnalysisShell from "./ResumeAnalysisShell";
import { useResumeAnalysisDashboard } from "./useResumeAnalysisDashboard";
import "../ResumeAnalysis.css";

const severityClass = (severity) => {
  if (severity === "HIGH") return "danger";
  if (severity === "MEDIUM") return "warning";
  return "success";
};

function ResumeSuggestions() {
  const { data, loading, error } = useResumeAnalysisDashboard();

  if (loading) {
    return <main className="analysis-page">Loading suggestions...</main>;
  }

  return (
    <ResumeAnalysisShell title="Resume Suggestions" subtitle="Priority-ranked recommendations with severity and expected ATS improvement.">
      {error && <div className="form-alert form-alert-error">{error}</div>}
      <section className="analysis-stack">
        {(data?.suggestions || []).map((item) => (
          <article className="analysis-suggestion" key={item.id}>
            <div className="analysis-suggestion-top">
              <span className={`analysis-badge ${severityClass(item.severity)}`}>{item.severity}</span>
              <strong>{item.category}</strong>
              <span>+{item.expectedAtsImprovement} ATS</span>
            </div>
            <p>{item.recommendation}</p>
          </article>
        ))}
      </section>
    </ResumeAnalysisShell>
  );
}

export default ResumeSuggestions;