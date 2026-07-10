import { useEffect, useState } from "react";
import ResumeAnalysisShell from "./ResumeAnalysisShell";
import { compareResumeAnalysisVersions, getResumeAnalysisHistory } from "../../../services/resumeAnalysisService";
import "../ResumeAnalysis.css";

function ResumeComparison() {
  const [history, setHistory] = useState([]);
  const [leftVersionId, setLeftVersionId] = useState("");
  const [rightVersionId, setRightVersionId] = useState("");
  const [comparison, setComparison] = useState(null);
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadHistory = async () => {
      try {
        const items = await getResumeAnalysisHistory();
        setHistory(items);
        if (items.length >= 2) {
          setRightVersionId(String(items[0].resumeVersionId));
          setLeftVersionId(String(items[1].resumeVersionId));
        }
      } catch (loadError) {
        setError(loadError.response?.data?.message || "Unable to load comparison history.");
      } finally {
        setLoading(false);
      }
    };

    loadHistory();
  }, []);

  const handleCompare = async (event) => {
    event.preventDefault();
    if (!leftVersionId || !rightVersionId) {
      setError("Choose two versions to compare.");
      return;
    }

    setWorking(true);
    setError("");

    try {
      const result = await compareResumeAnalysisVersions(leftVersionId, rightVersionId);
      setComparison(result);
    } catch (compareError) {
      setError(compareError.response?.data?.message || "Unable to compare resume versions.");
    } finally {
      setWorking(false);
    }
  };

  if (loading) {
    return <main className="analysis-page">Loading comparison...</main>;
  }

  return (
    <ResumeAnalysisShell title="Resume Comparison" subtitle="Compare any two analyzed versions and review the ATS delta plus changed fields.">
      {error && <div className="form-alert form-alert-error">{error}</div>}

      <form className="analysis-panel analysis-compare-form" onSubmit={handleCompare}>
        <label>
          <span>Left Version</span>
          <select value={leftVersionId} onChange={(event) => setLeftVersionId(event.target.value)}>
            <option value="">Select version</option>
            {history.map((item) => (
              <option key={item.resumeVersionId} value={item.resumeVersionId}>
                v{item.versionNumber} - ATS {item.atsScore}
              </option>
            ))}
          </select>
        </label>

        <label>
          <span>Right Version</span>
          <select value={rightVersionId} onChange={(event) => setRightVersionId(event.target.value)}>
            <option value="">Select version</option>
            {history.map((item) => (
              <option key={item.resumeVersionId} value={item.resumeVersionId}>
                v{item.versionNumber} - ATS {item.atsScore}
              </option>
            ))}
          </select>
        </label>

        <button className="analysis-button" type="submit" disabled={working}>
          {working ? "Comparing..." : "Compare Versions"}
        </button>
      </form>

      {comparison && (
        <section className="analysis-grid">
          <article className="analysis-card metric">
            <span>Left Score</span>
            <strong>{comparison.leftScore}</strong>
          </article>
          <article className="analysis-card metric">
            <span>Right Score</span>
            <strong>{comparison.rightScore}</strong>
          </article>
          <article className="analysis-card metric">
            <span>Delta</span>
            <strong>{comparison.scoreDelta >= 0 ? `+${comparison.scoreDelta}` : comparison.scoreDelta}</strong>
          </article>

          <article className="analysis-card">
            <h3>Improvements</h3>
            <ul className="analysis-list">
              {(comparison.improvements || []).map((item, index) => <li key={`${item}-${index}`}>{item}</li>)}
            </ul>
          </article>
          <article className="analysis-card">
            <h3>Regressions</h3>
            <ul className="analysis-list">
              {(comparison.regressions || []).map((item, index) => <li key={`${item}-${index}`}>{item}</li>)}
            </ul>
          </article>
          <article className="analysis-card">
            <h3>Changed Fields</h3>
            <ul className="analysis-list">
              {(comparison.changedFields || []).map((item, index) => <li key={`${item}-${index}`}>{item}</li>)}
            </ul>
          </article>
        </section>
      )}
    </ResumeAnalysisShell>
  );
}

export default ResumeComparison;