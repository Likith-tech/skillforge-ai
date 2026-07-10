import { Link } from "react-router-dom";
import ResumeAnalysisShell from "./ResumeAnalysisShell";
import { useResumeAnalysisDashboard } from "./useResumeAnalysisDashboard";
import "../ResumeAnalysis.css";

function ResumeAnalysisHistory() {
  const { data, loading, error } = useResumeAnalysisDashboard();

  if (loading) {
    return <main className="analysis-page">Loading analysis history...</main>;
  }

  return (
    <ResumeAnalysisShell title="Resume Analysis History" subtitle="Version-by-version ATS history with change summaries and timestamps.">
      {error && <div className="form-alert form-alert-error">{error}</div>}
      <section className="analysis-panel">
        <div className="analysis-panel-header">
          <h3>ATS History</h3>
          <Link to="/student/resume/analysis/compare">Compare versions</Link>
        </div>
        <table className="analysis-table">
          <thead>
            <tr>
              <th>Version</th>
              <th>ATS</th>
              <th>Uploaded</th>
              <th>Changes</th>
            </tr>
          </thead>
          <tbody>
            {(data?.history || []).map((item) => (
              <tr key={item.id}>
                <td>v{item.versionNumber}</td>
                <td>{item.atsScore}</td>
                <td>{item.uploadedAt ? new Date(item.uploadedAt).toLocaleString() : "Unknown"}</td>
                <td>{item.changesSummary}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </ResumeAnalysisShell>
  );
}

export default ResumeAnalysisHistory;