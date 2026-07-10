import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getJobs } from "../../services/jobPlatformService";
import "./JobPlatform.css";

function JobBoard() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("");
  const [location, setLocation] = useState("");

  useEffect(() => {
    const load = async () => {
      const response = await getJobs({ q: query, category, location, size: 50 });
      setJobs(response.items || []);
      setLoading(false);
    };

    load();
  }, [query, category, location]);

  return (
    <main className="job-platform-page">
      <section className="job-platform-shell">
        <header className="job-platform-header">
          <div>
            <h1>Job Board</h1>
            <p>Browse live openings and open any posting for the full description and fit signals.</p>
          </div>
          <div className="job-platform-actions">
            <Link className="job-link secondary" to="/student/jobs">Student Dashboard</Link>
          </div>
        </header>

        <section className="job-hero">
          <article className="job-hero-panel">
            <div className="job-form-grid">
              <label className="job-field">
                <span>Search</span>
                <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Job title, skill, or company" />
              </label>
              <label className="job-field">
                <span>Category</span>
                <input value={category} onChange={(event) => setCategory(event.target.value)} placeholder="Category" />
              </label>
              <label className="job-field job-full">
                <span>Location</span>
                <input value={location} onChange={(event) => setLocation(event.target.value)} placeholder="City or remote" />
              </label>
            </div>
          </article>
        </section>

        <div className="job-grid">
          <section className="job-card job-section">
            {loading ? <p>Loading jobs...</p> : (
              <ul className="job-list">
                {jobs.map((job) => (
                  <li className="job-list-item" key={job.id}>
                    <div className="job-list-item-header">
                      <div>
                        <h3 style={{ margin: 0 }}>{job.title}</h3>
                        <p style={{ margin: "6px 0 0" }}>{job.company?.name} · {job.location?.displayName}</p>
                      </div>
                      <span className="job-chip success">{job.status}</span>
                    </div>
                    <p>{job.description}</p>
                    <div className="job-tag-row">
                      <span className="job-chip">{job.employmentType}</span>
                      <span className="job-chip">{job.category?.name}</span>
                      <span className="job-chip">{job.currency} {job.salaryMin ?? "-"} to {job.salaryMax ?? "-"}</span>
                      <Link className="job-link secondary" to={`/jobs/${job.id}`}>View details</Link>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </section>
        </div>
      </section>
    </main>
  );
}

export default JobBoard;