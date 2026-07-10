import ResumeAnalysisShell from "./ResumeAnalysisShell";
import { useResumeAnalysisDashboard } from "./useResumeAnalysisDashboard";
import "../ResumeAnalysis.css";

const barHeight = (value) => `${Math.max(8, Math.min(100, value || 0))}%`;

function ResumeAnalytics() {
  const { data, loading, error } = useResumeAnalysisDashboard();

  if (loading) {
    return <main className="analysis-page">Loading analytics...</main>;
  }

  return (
    <ResumeAnalysisShell title="Resume Analytics" subtitle="Cards and charts for ATS trend, skills distribution, and completeness breakdown.">
      {error && <div className="form-alert form-alert-error">{error}</div>}

      <section className="analysis-grid analysis-grid-metrics">
        {[
          ["Overall ATS", data?.analytics?.overallAts],
          ["Skills", data?.analytics?.skillsCount],
          ["Projects", data?.analytics?.projectsCount],
          ["Experience", data?.analytics?.experienceCount],
          ["Education", data?.analytics?.educationCount],
          ["Certifications", data?.analytics?.certificationsCount],
          ["Languages", data?.analytics?.languagesCount],
          ["Completeness", data?.analytics?.completenessScore],
        ].map(([label, value]) => (
          <article className="analysis-card metric" key={label}>
            <span>{label}</span>
            <strong>{value ?? 0}</strong>
          </article>
        ))}
      </section>

      <section className="analysis-grid analysis-grid-2">
        <article className="analysis-card">
          <h3>ATS Trend</h3>
          <div className="analysis-chart bars">
            {(data?.analytics?.atsTrend || []).map((point) => (
              <div className="analysis-chart-column" key={point.label}>
                <span className="analysis-chart-value">{point.value}</span>
                <div className="analysis-chart-bar-wrap">
                  <div className="analysis-chart-bar" style={{ height: barHeight(point.value) }} />
                </div>
                <small>{point.label}</small>
              </div>
            ))}
          </div>
        </article>

        <article className="analysis-card">
          <h3>Skills Distribution</h3>
          <div className="analysis-chart bars">
            {(data?.analytics?.skillsDistribution || []).map((point) => (
              <div className="analysis-chart-column" key={point.label}>
                <span className="analysis-chart-value">{point.value}</span>
                <div className="analysis-chart-bar-wrap">
                  <div className="analysis-chart-bar" style={{ height: barHeight(point.value), background: point.color }} />
                </div>
                <small>{point.label}</small>
              </div>
            ))}
          </div>
        </article>
      </section>

      <section className="analysis-panel">
        <h3>Completeness Breakdown</h3>
        <div className="analysis-score-list">
          {(data?.analytics?.completenessBreakdown || []).map((item) => (
            <div className="analysis-score-row" key={item.label}>
              <div className="analysis-score-row-label">
                <span>{item.label}</span>
                <strong>{item.value}</strong>
              </div>
              <div className="analysis-progress">
                <span style={{ width: `${item.value || 0}%`, background: item.color }} />
              </div>
            </div>
          ))}
        </div>
      </section>
    </ResumeAnalysisShell>
  );
}

export default ResumeAnalytics;