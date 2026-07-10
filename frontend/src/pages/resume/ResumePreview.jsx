import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  downloadResumeBlob,
  getResume,
  getResumePreviewBlob,
  saveBlob,
} from "../../services/resumeService";
import "./Resume.css";
import { formatDateTime, formatFileSize, readableFileType } from "./resumeFormat";

function ResumePreview() {
  const [resume, setResume] = useState(null);
  const [previewUrl, setPreviewUrl] = useState("");
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    let objectUrl = "";

    const loadPreview = async () => {
      try {
        const metadata = await getResume();
        setResume(metadata);

        if (!metadata.previewAvailable) {
          setError("Preview is available only for PDF resumes.");
          return;
        }

        const blob = await getResumePreviewBlob();
        objectUrl = URL.createObjectURL(blob);
        setPreviewUrl(objectUrl);
      } catch (previewError) {
        setError(previewError.response?.data?.message || "Unable to load resume preview.");
      } finally {
        setLoading(false);
      }
    };

    loadPreview();

    return () => {
      if (objectUrl) {
        URL.revokeObjectURL(objectUrl);
      }
    };
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

  if (loading) {
    return <main className="resume-page">Loading preview...</main>;
  }

  return (
    <main className="resume-page">
      <section className="resume-header">
        <div>
          <h1>Resume Preview</h1>
          <p>Preview your active PDF resume in the browser.</p>
        </div>
        <div className="resume-actions">
          <Link className="resume-link-button secondary" to="/student/resume">
            Back
          </Link>
          <button className="resume-button" type="button" onClick={handleDownload} disabled={working}>
            Download
          </button>
        </div>
      </section>

      {error && <div className="form-alert form-alert-error">{error}</div>}

      {resume && (
        <section className="resume-panel" style={{ marginBottom: 20 }}>
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
              <span>Last Updated</span>
              <strong>{formatDateTime(resume.updatedAt)}</strong>
            </div>
          </div>
        </section>
      )}

      {previewUrl && <iframe className="resume-preview-frame" title="Resume preview" src={previewUrl} />}
    </main>
  );
}

export default ResumePreview;
