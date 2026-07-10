import ResumeAnalysisShell from "./ResumeAnalysisShell";
import { useResumeAnalysisDashboard } from "./useResumeAnalysisDashboard";
import "../ResumeAnalysis.css";

const formatPercent = (value) => `${Math.max(0, Math.min(100, value || 0))}%`;

function ResumeATSReport() {
  const { data, loading, error } = useResumeAnalysisDashboard();

  if (loading) {
    return <main className="analysis-page">Loading ATS report...</main>;
  }

  return (
    <ResumeAnalysisShell title="ATS Report" subtitle="Category-by-category scoring with progress bars and score bands.">
      {error && <div className="form-alert form-alert-error">{error}</div>}
      <section className="analysis-panel">
        <div className="analysis-score-list">
          {[
            ["Structure", data?.scores?.structureScore],
            ["Formatting", data?.scores?.formattingScore],
            ["Skills", data?.scores?.skillsScore],
            ["Education", data?.scores?.educationScore],
            ["Projects", data?.scores?.projectsScore],
            ["Experience", data?.scores?.experienceScore],
            ["Keywords", data?.scores?.keywordsScore],
            ["Readability", data?.scores?.readabilityScore],
            ["Contact Information", data?.scores?.contactInformationScore],
            ["Completeness", data?.scores?.completenessScore],
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
    </ResumeAnalysisShell>
  );
}

export default ResumeATSReport;