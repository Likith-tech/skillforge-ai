import { useEffect, useState } from "react";
import {
  getCandidateTimeline,
  getJob,
  getMyJobs,
  getRankedApplicants,
  rejectApplicant,
  scheduleInterview,
  sendOffer,
  shortlistApplicant,
  withdrawOffer,
} from "../../services/jobPlatformService";
import "../jobs/JobPlatform.css";

function RecruiterWorkflow() {
  const [jobs, setJobs] = useState([]);
  const [selectedJobId, setSelectedJobId] = useState("");
  const [applicants, setApplicants] = useState([]);
  const [timeline, setTimeline] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadJobs = async () => {
    const data = await getMyJobs();
    setJobs(data || []);
    if (!selectedJobId && data?.length) {
      setSelectedJobId(String(data[0].id));
    }
    setLoading(false);
  };

  const loadApplicants = async (jobId) => {
    if (!jobId) return;
    const data = await getRankedApplicants(jobId);
    setApplicants(data || []);
    setTimeline(await getCandidateTimeline(jobId));
  };

  useEffect(() => {
    loadJobs();
  }, []);

  useEffect(() => {
    loadApplicants(selectedJobId);
  }, [selectedJobId]);

  if (loading) {
    return <main className="job-platform-page">Loading recruiter workflow...</main>;
  }

  return (
    <main className="job-platform-page">
      <section className="job-platform-shell">
        <header className="job-platform-header">
          <div>
            <h1>Recruiter Workflow</h1>
            <p>Applicant ranking, shortlist/reject, interviews, offers, and candidate status timeline.</p>
          </div>
        </header>

        <div className="job-grid">
          <section className="job-card job-section">
            <label className="job-field">
              <span>Job</span>
              <select className="job-select" value={selectedJobId} onChange={(e) => setSelectedJobId(e.target.value)}>
                {jobs.map((job) => <option value={job.id} key={job.id}>{job.title}</option>)}
              </select>
            </label>
            <div className="job-divider" />
            <ul className="job-list">
              {applicants.map((application) => (
                <li className="job-list-item" key={application.id}>
                  <div className="job-list-item-header">
                    <div>
                      <strong>Student {application.studentId}</strong>
                      <p className="job-muted">Applied {application.appliedAt} · {application.status}</p>
                    </div>
                    <span className="job-chip success">{application.job?.title}</span>
                  </div>
                  <div className="job-chip-row">
                    <button className="job-button secondary" type="button" onClick={async () => { await shortlistApplicant(application.id); await loadApplicants(selectedJobId); }}>Shortlist</button>
                    <button className="job-button ghost" type="button" onClick={async () => { await rejectApplicant(application.id); await loadApplicants(selectedJobId); }}>Reject</button>
                    <button className="job-button ghost" type="button" onClick={async () => { await scheduleInterview(application.id, new Date().toISOString(), "", ""); await loadApplicants(selectedJobId); }}>Interview</button>
                    <button className="job-button ghost" type="button" onClick={async () => { await sendOffer(application.id, "Offer from SkillForge AI", "Congratulations"); await loadApplicants(selectedJobId); }}>Offer</button>
                    <button className="job-button ghost" type="button" onClick={async () => { await withdrawOffer(application.id); await loadApplicants(selectedJobId); }}>Withdraw</button>
                  </div>
                </li>
              ))}
            </ul>
          </section>

          <aside className="job-card job-sidebar">
            <h3>Status Timeline</h3>
            <ul className="job-list">
              {timeline.map((item) => <li className="job-list-item" key={item.id}>{item.status} · {item.interviewAt || item.appliedAt || item.createdAt}</li>)}
            </ul>
          </aside>
        </div>
      </section>
    </main>
  );
}

export default RecruiterWorkflow;