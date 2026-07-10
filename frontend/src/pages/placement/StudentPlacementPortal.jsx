import { useEffect, useState } from "react";
import {
  getAppliedJobs,
  getApplicationsByStatus,
  getInterviewSchedules,
  getOfferLetters,
  getPlacementTimeline,
  getSavedJobs,
  removeSavedJob,
  saveJob,
} from "../../services/jobPlatformService";
import "../jobs/JobPlatform.css";

function StudentPlacementPortal() {
  const [loading, setLoading] = useState(true);
  const [appliedJobs, setAppliedJobs] = useState([]);
  const [savedJobs, setSavedJobs] = useState([]);
  const [interviews, setInterviews] = useState([]);
  const [offers, setOffers] = useState([]);
  const [timeline, setTimeline] = useState([]);
  const [rejected, setRejected] = useState([]);

  const load = async () => {
    const [applied, saved, interviewData, offerData, timelineData, rejectedData] = await Promise.all([
      getAppliedJobs(),
      getSavedJobs(),
      getInterviewSchedules(),
      getOfferLetters(),
      getPlacementTimeline(),
      getApplicationsByStatus("REJECTED"),
    ]);
    setAppliedJobs(applied || []);
    setSavedJobs(saved || []);
    setInterviews(interviewData || []);
    setOffers(offerData || []);
    setTimeline(timelineData || []);
    setRejected(rejectedData || []);
    setLoading(false);
  };

  useEffect(() => {
    load();
  }, []);

  if (loading) {
    return <main className="job-platform-page">Loading placement portal...</main>;
  }

  return (
    <main className="job-platform-page">
      <section className="job-platform-shell">
        <header className="job-platform-header">
          <div>
            <h1>Placement Portal</h1>
            <p>Track applications, interviews, saved jobs, offers, and placement activity in one place.</p>
          </div>
        </header>
        <div className="job-grid">
          <article className="job-card job-stat"><span>Applied Jobs</span><strong>{appliedJobs.length}</strong></article>
          <article className="job-card job-stat"><span>Saved Jobs</span><strong>{savedJobs.length}</strong></article>
          <article className="job-card job-stat"><span>Interviews</span><strong>{interviews.length}</strong></article>
          <article className="job-card job-stat"><span>Offers</span><strong>{offers.length}</strong></article>

          <section className="job-card job-section">
            <h3>Applied Jobs</h3>
            <ul className="job-list">
              {appliedJobs.map((item) => (
                <li className="job-list-item" key={item.id}>
                  <strong>{item.job?.title}</strong>
                  <p className="job-muted">{item.job?.company?.name} · {item.status}</p>
                </li>
              ))}
            </ul>
          </section>

          <aside className="job-card job-sidebar">
            <h3>Saved Jobs</h3>
            <ul className="job-list">
              {savedJobs.map((item) => (
                <li className="job-list-item" key={item.id}>
                  <strong>{item.job?.title}</strong>
                  <div className="job-platform-actions" style={{ marginTop: 10 }}>
                    <button className="job-button secondary" type="button" onClick={async () => { await removeSavedJob(item.job?.id); await load(); }}>Remove</button>
                  </div>
                </li>
              ))}
            </ul>
          </aside>

          <section className="job-card job-section">
            <h3>Interview Schedule</h3>
            <ul className="job-list">
              {interviews.map((item) => (
                <li className="job-list-item" key={item.id}>
                  <strong>{item.job?.title}</strong>
                  <p className="job-muted">{item.interviewAt} · {item.meetingLink || "Pending link"}</p>
                </li>
              ))}
            </ul>
          </section>

          <aside className="job-card job-sidebar">
            <h3>Offer Letters</h3>
            <ul className="job-list">
              {offers.map((item) => (
                <li className="job-list-item" key={item.id}>
                  <strong>{item.offerTitle}</strong>
                  <p className="job-muted">{item.job?.title} · {item.salaryAmount || "Offer pending details"}</p>
                </li>
              ))}
            </ul>
          </aside>

          <section className="job-card job-section">
            <h3>Placement Timeline</h3>
            <ul className="job-list">
              {timeline.map((item) => (
                <li className="job-list-item" key={item.id}>
                  <strong>{item.status}</strong>
                  <p className="job-muted">{item.description}</p>
                </li>
              ))}
            </ul>
          </section>

          <aside className="job-card job-sidebar">
            <h3>Rejected Applications</h3>
            <ul className="job-list">
              {rejected.map((item) => <li className="job-list-item" key={item.id}>{item.job?.title}</li>)}
            </ul>
          </aside>
        </div>
      </section>
    </main>
  );
}

export default StudentPlacementPortal;