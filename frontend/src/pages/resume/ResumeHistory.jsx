import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  downloadResumeVersionBlob,
  getResumeHistory,
  getResumeVersionPreviewBlob,
  saveBlob,
} from "../../services/resumeService";
import "./Resume.css";
import { formatDateTime, formatFileSize, readableFileType } from "./resumeFormat";

function ResumeHistory() {
  const [versions, setVersions] = useState([]);
  const [previewUrl, setPreviewUrl] = useState("");
  const [previewName, setPreviewName] = useState("");
  const [loading, setLoading] = useState(true);
  const [workingId, setWorkingId] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadHistory = async () => {
      try {
        const data = await getResumeHistory();
        setVersions(data);
      } catch (historyError) {
        setError("Unable to load resume history.");
      } finally {
        setLoading(false);
      }
    };

    loadHistory();
  }, []);

  useEffect(() => {
    return () => {
      if (previewUrl) {
        URL.revokeObjectURL(previewUrl);
      }
    };
  }, [previewUrl]);

  const handleDownload = async (versionId) => {
    setWorkingId(versionId);
    setError("");

    try {
      saveBlob(await downloadResumeVersionBlob(versionId));
    } catch (downloadError) {
      setError("Unable to download resume version.");
    } finally {
      setWorkingId(null);
    }
  };

  const handlePreview = async (version) => {
    setWorkingId(version.id);
    setError("");

    try {
      const blob = await getResumeVersionPreviewBlob(version.id);
      if (previewUrl) {
        URL.revokeObjectURL(previewUrl);
      }
      setPreviewUrl(URL.createObjectURL(blob));
      setPreviewName(`Version ${version.versionNumber}: ${version.originalFileName}`);
    } catch (previewError) {
      setError(previewError.response?.data?.message || "Unable to preview resume version.");
    } finally {
      setWorkingId(null);
    }
  };

  if (loading) {
    return <main className="resume-page">Loading resume history...</main>;
  }

  return (
    <main className="resume-page">
      <section className="resume-header">
        <div>
          <h1>Resume History</h1>
          <p>Review and download previous resume versions.</p>
        </div>
        <Link className="resume-link-button secondary" to="/student/resume">
          Back
        </Link>
      </section>

      {error && <div className="form-alert form-alert-error">{error}</div>}

      <section className="resume-panel">
        {versions.length === 0 ? (
          <p className="resume-empty">No resume versions are available.</p>
        ) : (
          <table className="resume-history-table">
            <thead>
              <tr>
                <th>Version</th>
                <th>File</th>
                <th>Type</th>
                <th>Size</th>
                <th>Uploaded</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {versions.map((version) => (
                <tr key={version.id}>
                  <td>v{version.versionNumber}</td>
                  <td>{version.originalFileName}</td>
                  <td>{readableFileType(version.fileType)}</td>
                  <td>{formatFileSize(version.fileSize)}</td>
                  <td>{formatDateTime(version.uploadedAt)}</td>
                  <td>
                    <div className="resume-row-actions">
                      {version.previewAvailable && (
                        <button
                          className="resume-button secondary"
                          type="button"
                          onClick={() => handlePreview(version)}
                          disabled={workingId === version.id}
                        >
                          Preview
                        </button>
                      )}
                      <button
                        className="resume-button secondary"
                        type="button"
                        onClick={() => handleDownload(version.id)}
                        disabled={workingId === version.id}
                      >
                        Download
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      {previewUrl && (
        <section style={{ marginTop: 20 }}>
          <h2>{previewName}</h2>
          <iframe className="resume-preview-frame" title="Resume version preview" src={previewUrl} />
        </section>
      )}
    </main>
  );
}

export default ResumeHistory;
