import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  deleteResume,
  downloadResumeBlob,
  getResume,
  saveBlob,
} from "../../services/resumeService";
import "./Resume.css";
import { formatDateTime, formatFileSize, readableFileType } from "./resumeFormat";

function ResumeDashboard() {
  const [resume, setResume] = useState(null);
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    const loadResume = async () => {
      try {
        const data = await getResume();
        setResume(data);
      } catch (loadError) {
        if (loadError.response?.status !== 404) {
          setError("Unable to load resume.");
        }
      } finally {
        setLoading(false);
      }
    };

    loadResume();
  }, []);

  const handleDownload = async () => {
    setWorking(true);
    setError("");

    try {
      saveBlob(await downloadResumeBlob());
    } catch (downloadError) {
      setError("Unable to download resume.");
    } finally {
      setWorking(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("Delete your active resume? Resume history will remain available.")) {
      return;
    }

    setWorking(true);
    setError("");
    setMessage("");

    try {
      await deleteResume();
      setResume(null);
      setMessage("Resume deleted successfully.");
    } catch (deleteError) {
      setError(deleteError.response?.data?.message || "Unable to delete resume.");
    } finally {
      setWorking(false);
    }
  };

  if (loading) {
    return <main className="resume-page">Loading resume...</main>;
  }

  return (
    <main className="resume-page">
      <section className="resume-header">
        <div>
          <h1>Resume</h1>
          <p>Manage your active resume and version history.</p>
        </div>
        <div className="resume-actions">
          <Link className="resume-link-button" to="/student/resume/upload">
            {resume ? "Replace Resume" : "Upload Resume"}
          </Link>
          <Link className="resume-link-button secondary" to="/student/resume/history">
            History
          </Link>
          <Link className="resume-link-button secondary" to="/student/resume/analysis">
            Analysis Dashboard
          </Link>
        </div>
      </section>

      {error && <div className="form-alert form-alert-error">{error}</div>}
      {message && <div className="form-alert form-alert-success">{message}</div>}

      {!resume ? (
        <section className="resume-panel resume-empty">
          No active resume is uploaded.
        </section>
      ) : (
        <section className="resume-panel">
          <div className="resume-meta-grid">
            <div className="resume-meta-item">
              <span>File Name</span>
              <strong>{resume.originalFileName}</strong>
            </div>
            <div className="resume-meta-item">
              <span>File Type</span>
              <strong>{readableFileType(resume.fileType)}</strong>
            </div>
            <div className="resume-meta-item">
              <span>File Size</span>
              <strong>{formatFileSize(resume.fileSize)}</strong>
            </div>
            <div className="resume-meta-item">
              <span>Status</span>
              <strong>{resume.status}</strong>
            </div>
            <div className="resume-meta-item">
              <span>Uploaded</span>
              <strong>{formatDateTime(resume.uploadedAt)}</strong>
            </div>
            <div className="resume-meta-item">
              <span>Last Updated</span>
              <strong>{formatDateTime(resume.updatedAt)}</strong>
            </div>
          </div>

          <div className="resume-actions" style={{ marginTop: 24 }}>
            {resume.previewAvailable && (
              <Link className="resume-link-button secondary" to="/student/resume/preview">
                Preview
              </Link>
            )}
            <button className="resume-button secondary" type="button" onClick={handleDownload} disabled={working}>
              Download
            </button>
            <button className="resume-button danger" type="button" onClick={handleDelete} disabled={working}>
              Delete
            </button>
          </div>
        </section>
      )}
    </main>
  );
}

export default ResumeDashboard;
