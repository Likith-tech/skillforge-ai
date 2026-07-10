import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getJob } from "../../services/jobPlatformService";
import "./JobPlatform.css";

function JobDetail() {
  const { jobId } = useParams();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      setJob(await getJob(jobId));
      setLoading(false);
    };

    load();
  }, [jobId]);

  if (loading) {
    return <main className="job-platform-page">Loading job...</main>;
  }

  return (
    <main className="job-platform-page">
      <section className="job-platform-shell">
        <header className="job-platform-header">
          <div>
            <h1>{job?.title}</h1>
            <p>{job?.company?.name} · {job?.location?.displayName}</p>
          </div>
          <div className="job-platform-actions">
            <Link className="job-link secondary" to="/jobs">Back to board</Link>
          </div>
        </header>

        <div className="job-grid">
          <article className="job-card job-section">
            <h3>About the role</h3>
            <p>{job?.description}</p>
            <div className="job-chip-row">
              <span className="job-chip">{job?.employmentType}</span>
              <span className="job-chip">{job?.category?.name}</span>
              <span className="job-chip">Deadline {job?.deadline || "Open"}</span>
            </div>
            <div className="job-divider" />
            <h3>Skills</h3>
            <div className="job-chip-row">
              {(job?.skills || []).map((skill) => (
                <span className="job-chip" key={skill.id}>{skill.skillName}</span>
              ))}
            </div>
          </article>
          <aside className="job-card job-sidebar">
            <h3>Compensation</h3>
            <p className="job-muted">{job?.currency} {job?.salaryMin ?? "-"} to {job?.salaryMax ?? "-"}</p>
            <div className="job-divider" />
            <h3>Recruiter</h3>
            <p className="job-muted">Recruiter ID {job?.recruiterId}</p>
          </aside>
        </div>
      </section>
    </main>
  );
}

export default JobDetail;