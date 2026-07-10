import { useEffect, useState } from "react";
import { getMyJobs, createJob, updateJobStatus } from "../../services/jobPlatformService";
import "./JobPlatform.css";

const emptyForm = {
  title: "",
  companyName: "",
  description: "",
  categoryName: "",
  locationDisplayName: "",
  employmentType: "FULL_TIME",
  minimumCgpa: "",
  minimumExperienceYears: "",
  salaryMin: "",
  salaryMax: "",
  currency: "INR",
  deadline: "",
  status: "ACTIVE",
  requiredSkills: "",
  preferredSkills: "",
};

const splitSkills = (value) =>
  value
    .split("\n")
    .map((line) => line.trim())
    .filter(Boolean)
    .map((skill) => ({ skillName: skill }));

function RecruiterJobs() {
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [jobs, setJobs] = useState([]);
  const [form, setForm] = useState(emptyForm);

  const loadJobs = async () => {
    try {
      setJobs(await getMyJobs());
    } catch (loadError) {
      setError(loadError.response?.data?.message || "Unable to load your jobs.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadJobs();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setWorking(true);
    setError("");
    setMessage("");

    try {
      await createJob({
        ...form,
        minimumCgpa: form.minimumCgpa ? Number(form.minimumCgpa) : null,
        minimumExperienceYears: form.minimumExperienceYears ? Number(form.minimumExperienceYears) : null,
        salaryMin: form.salaryMin ? Number(form.salaryMin) : null,
        salaryMax: form.salaryMax ? Number(form.salaryMax) : null,
        deadline: form.deadline || null,
        requiredSkills: splitSkills(form.requiredSkills),
        preferredSkills: splitSkills(form.preferredSkills),
      });
      setForm(emptyForm);
      setMessage("Job created successfully.");
      await loadJobs();
    } catch (submitError) {
      setError(submitError.response?.data?.message || "Unable to create job.");
    } finally {
      setWorking(false);
    }
  };

  const handleStatusChange = async (jobId, status) => {
    setWorking(true);
    setError("");
    try {
      await updateJobStatus(jobId, status);
      await loadJobs();
    } catch (statusError) {
      setError(statusError.response?.data?.message || "Unable to update job status.");
    } finally {
      setWorking(false);
    }
  };

  if (loading) {
    return <main className="job-platform-page">Loading recruiter jobs...</main>;
  }

  return (
    <main className="job-platform-page">
      <section className="job-platform-shell">
        <header className="job-platform-header">
          <div>
            <h1>Recruiter Command Center</h1>
            <p>Create jobs, update status, and review your live openings.</p>
          </div>
          <div className="job-platform-actions">
            <a className="job-link secondary" href="/jobs">Public job board</a>
          </div>
        </header>

        {(error || message) && (
          <div style={{ padding: "0 28px" }}>
            {error && <div className="form-alert form-alert-error">{error}</div>}
            {message && <div className="form-alert form-alert-success">{message}</div>}
          </div>
        )}

        <div className="job-grid">
          <section className="job-form job-section">
            <h2>Create Job</h2>
            <p className="job-muted">Every posting is persisted in PostgreSQL and becomes available to the recommendation engine immediately.</p>
            <form onSubmit={handleSubmit} className="job-form-grid">
              {[
                ["title", "Title"],
                ["companyName", "Company"],
                ["categoryName", "Category"],
                ["locationDisplayName", "Location"],
                ["minimumCgpa", "Minimum CGPA"],
                ["minimumExperienceYears", "Minimum Experience (Years)"],
                ["salaryMin", "Salary Min"],
                ["salaryMax", "Salary Max"],
                ["deadline", "Deadline"],
              ].map(([name, label]) => (
                <label key={name} className="job-field">
                  <span>{label}</span>
                  <input name={name} value={form[name]} onChange={handleChange} />
                </label>
              ))}

              <label className="job-field">
                <span>Employment Type</span>
                <select name="employmentType" value={form.employmentType} onChange={handleChange}>
                  <option value="FULL_TIME">Full Time</option>
                  <option value="PART_TIME">Part Time</option>
                  <option value="INTERNSHIP">Internship</option>
                  <option value="CONTRACT">Contract</option>
                  <option value="FREELANCE">Freelance</option>
                </select>
              </label>

              <label className="job-field">
                <span>Status</span>
                <select name="status" value={form.status} onChange={handleChange}>
                  <option value="ACTIVE">Active</option>
                  <option value="PAUSED">Paused</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </label>

              <label className="job-field job-full">
                <span>Description</span>
                <textarea name="description" value={form.description} onChange={handleChange} />
              </label>

              <label className="job-field job-full">
                <span>Required Skills - one per line</span>
                <textarea name="requiredSkills" value={form.requiredSkills} onChange={handleChange} />
              </label>

              <label className="job-field job-full">
                <span>Preferred Skills - one per line</span>
                <textarea name="preferredSkills" value={form.preferredSkills} onChange={handleChange} />
              </label>

              <div className="job-full job-platform-actions">
                <button className="job-button" type="submit" disabled={working}>{working ? "Saving..." : "Create Job"}</button>
              </div>
            </form>
          </section>

          <aside className="job-card job-sidebar">
            <h3>Your Jobs</h3>
            <p className="job-muted">Current openings managed by your recruiter account.</p>
            <div className="job-divider" />
            <ul className="job-list">
              {jobs.map((job) => (
                <li className="job-list-item" key={job.id}>
                  <div className="job-list-item-header">
                    <div>
                      <h4 style={{ margin: 0 }}>{job.title}</h4>
                      <p style={{ margin: "6px 0 0" }}>{job.company?.name} · {job.location?.displayName}</p>
                    </div>
                    <span className="job-chip">{job.status}</span>
                  </div>
                  <div className="job-chip-row">
                    <span className="job-chip">{job.employmentType}</span>
                    <span className="job-chip">{job.category?.name}</span>
                  </div>
                  <div className="job-platform-actions" style={{ marginTop: 12 }}>
                    <button className="job-button secondary" type="button" onClick={() => handleStatusChange(job.id, "ACTIVE")}>Activate</button>
                    <button className="job-button ghost" type="button" onClick={() => handleStatusChange(job.id, "PAUSED")}>Pause</button>
                    <button className="job-button ghost" type="button" onClick={() => handleStatusChange(job.id, "CLOSED")}>Close</button>
                  </div>
                </li>
              ))}
            </ul>
          </aside>
        </div>
      </section>
    </main>
  );
}

export default RecruiterJobs;