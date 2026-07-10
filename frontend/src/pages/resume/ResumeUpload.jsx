import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getResume, replaceResume, uploadResume } from "../../services/resumeService";
import "./Resume.css";

function ResumeUpload() {
  const navigate = useNavigate();
  const [hasResume, setHasResume] = useState(false);
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const checkResume = async () => {
      try {
        await getResume();
        setHasResume(true);
      } catch (loadError) {
        if (loadError.response?.status !== 404) {
          setError("Unable to check current resume.");
        }
      } finally {
        setLoading(false);
      }
    };

    checkResume();
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (!file) {
      setError("Choose a PDF or DOCX resume.");
      return;
    }

    setSubmitting(true);

    try {
      if (hasResume) {
        await replaceResume(file);
      } else {
        await uploadResume(file);
      }
      navigate("/student/resume");
    } catch (uploadError) {
      setError(uploadError.response?.data?.message || "Unable to save resume.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <main className="resume-page">Loading resume form...</main>;
  }

  return (
    <main className="resume-page">
      <section className="resume-header">
        <div>
          <h1>{hasResume ? "Replace Resume" : "Upload Resume"}</h1>
          <p>Upload a PDF or DOCX resume up to 5 MB.</p>
        </div>
        <Link className="resume-link-button secondary" to="/student/resume">
          Back
        </Link>
      </section>

      <form className="resume-panel resume-upload-form" onSubmit={handleSubmit}>
        {error && <div className="form-alert form-alert-error">{error}</div>}

        <label className="resume-upload-label">
          <span>Resume File</span>
          <input
            type="file"
            accept=".pdf,.docx,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            onChange={(event) => setFile(event.target.files?.[0] || null)}
            required
          />
        </label>

        <button className="resume-button" type="submit" disabled={submitting}>
          {submitting ? "Saving..." : hasResume ? "Replace Resume" : "Upload Resume"}
        </button>
      </form>
    </main>
  );
}

export default ResumeUpload;
