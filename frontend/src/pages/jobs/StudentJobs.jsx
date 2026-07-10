import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  getJobInsights,
  getPlacementScore,
  getRecommendations,
  getSkillGaps,
  getStudentDashboard,
  getRoadmaps,
  refreshRecommendations,
} from "../../services/jobPlatformService";
import "./JobPlatform.css";

const safeList = (value) => (Array.isArray(value) ? value : []);

function StudentJobs() {
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState("");
  const [dashboard, setDashboard] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [skillGaps, setSkillGaps] = useState([]);
  const [placementScore, setPlacementScore] = useState(null);
  const [roadmaps, setRoadmaps] = useState([]);
  const [insights, setInsights] = useState([]);

  useEffect(() => {
    const load = async () => {
      try {
        const [dashboardData, recommendationData, skillGapData, placementData, roadmapData, insightData] = await Promise.all([
          getStudentDashboard(),
          getRecommendations(),
          getSkillGaps(),
          getPlacementScore(),
          getRoadmaps(),
          getJobInsights(),
        ]);
        setDashboard(dashboardData);
        setRecommendations(safeList(recommendationData));
        setSkillGaps(safeList(skillGapData));
        setPlacementScore(placementData);
        setRoadmaps(safeList(roadmapData));
        setInsights(safeList(insightData));
      } catch (loadError) {
        setError(loadError.response?.data?.message || "Unable to load job recommendations.");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  const handleRefresh = async () => {
    setWorking(true);
    setError("");
    try {
      await refreshRecommendations();
      const [recommendationData, skillGapData, placementData, roadmapData, dashboardData, insightData] = await Promise.all([
        getRecommendations(),
        getSkillGaps(),
        getPlacementScore(),
        getRoadmaps(),
        getStudentDashboard(),
        getJobInsights(),
      ]);
      setRecommendations(safeList(recommendationData));
      setSkillGaps(safeList(skillGapData));
      setPlacementScore(placementData);
      setRoadmaps(safeList(roadmapData));
      setDashboard(dashboardData);
      setInsights(safeList(insightData));
    } catch (refreshError) {
      setError(refreshError.response?.data?.message || "Unable to refresh recommendations.");
    } finally {
      setWorking(false);
    }
  };

  if (loading) {
    return <main className="job-platform-page">Loading job platform...</main>;
  }

  const topRecommendation = recommendations[0];
  const topGap = skillGaps[0];

  return (
    <main className="job-platform-page">
      <section className="job-platform-shell">
        <header className="job-platform-header">
          <div>
            <h1>Career Radar</h1>
            <p>Job recommendations, skill gaps, placement readiness, and roadmap planning powered by your latest resume analysis.</p>
          </div>
          <div className="job-platform-actions">
            <button className="job-button" type="button" onClick={handleRefresh} disabled={working}>
              {working ? "Refreshing..." : "Refresh Signals"}
            </button>
            <Link className="job-link secondary" to="/student/resume/analysis">
              Resume Analysis
            </Link>
          </div>
        </header>

        {error && <div className="form-alert form-alert-error" style={{ margin: "0 28px 18px" }}>{error}</div>}

        <section className="job-hero">
          <article className="job-hero-panel">
            <p className="job-muted">Placement readiness</p>
            <h2 style={{ fontSize: 40, margin: "6px 0 12px" }}>{placementScore?.overallScore ?? 0}/100</h2>
            <p>{dashboard?.resumeStatus || "Resume status unavailable"}</p>
            <div className="job-chip-row">
              <span className="job-chip">ATS {dashboard?.atsTrend?.[0]?.value ?? 0}</span>
              <span className="job-chip success">{recommendations.length} recommendations</span>
              <span className="job-chip warning">{skillGaps.length} skill gaps</span>
            </div>
          </article>
          <article className="job-hero-panel">
            <p className="job-muted">Top recommendation</p>
            <h3 style={{ marginTop: 6 }}>{topRecommendation?.job?.title || "No recommendations yet"}</h3>
            <p>{topRecommendation?.job?.company?.name || "Add more resume signals to improve matching."}</p>
            <div className="job-chip-row">
              <span className="job-chip">Match {topRecommendation?.matchPercentage ?? 0}%</span>
              <span className="job-chip">Confidence {topRecommendation?.recommendationConfidence ?? 0}%</span>
            </div>
          </article>
        </section>

        <div className="job-grid">
          <article className="job-card job-stat">
            <span>Applied Jobs</span>
            <strong>{dashboard?.appliedJobsCount ?? 0}</strong>
          </article>
          <article className="job-card job-stat">
            <span>Roadmaps</span>
            <strong>{roadmaps.length}</strong>
          </article>
          <article className="job-card job-stat">
            <span>Top Match</span>
            <strong>{topRecommendation?.matchPercentage ?? 0}%</strong>
          </article>
          <article className="job-card job-stat">
            <span>Top Gap</span>
            <strong>{topGap?.priorityScore ?? 0}</strong>
          </article>

          <section className="job-card job-section">
            <div className="job-list-item-header">
              <div>
                <h3>Recommended Jobs</h3>
                <p className="job-muted">Ranked using ATS score, parsed skills, projects, education, and experience.</p>
              </div>
              <Link className="job-link ghost" to="/jobs">Browse all jobs</Link>
            </div>
            <div className="job-divider" />
            <ul className="job-list">
              {recommendations.slice(0, 5).map((item) => (
                <li className="job-list-item" key={item.id}>
                  <div className="job-list-item-header">
                    <div>
                      <h4 style={{ margin: 0 }}>{item.job?.title}</h4>
                      <p style={{ margin: "6px 0 0" }}>{item.job?.company?.name} · {item.job?.location?.displayName}</p>
                    </div>
                    <span className="job-chip success">{item.matchPercentage}% match</span>
                  </div>
                  <p>{item.reasonForRecommendation}</p>
                  <div className="job-tag-row">
                    <span className="job-chip">{item.job?.employmentType}</span>
                    <span className="job-chip">Confidence {item.recommendationConfidence}%</span>
                    <Link className="job-link secondary" to={`/jobs/${item.jobId}`}>Open</Link>
                  </div>
                </li>
              ))}
            </ul>
          </section>

          <aside className="job-card job-sidebar">
            <h3>Skill Gap Snapshot</h3>
            <p className="job-muted">Focus first on the highest priority missing skills.</p>
            <div className="job-divider" />
            <ul className="job-bullet-list">
              {skillGaps.slice(0, 5).map((gap) => (
                <li key={gap.id} className="job-list-item">
                  <strong>{gap.jobId}</strong>
                  <p className="job-muted" style={{ marginBottom: 8 }}>{gap.difficulty} · {gap.estimatedLearningTimeHours} hours</p>
                  <div className="job-chip-row">
                    {safeList(gap.missingSkills).slice(0, 3).map((skill) => <span className="job-chip warning" key={skill}>{skill}</span>)}
                  </div>
                </li>
              ))}
            </ul>
          </aside>

          <section className="job-card job-section">
            <h3>Roadmaps</h3>
            <p className="job-muted">Each roadmap is built from the specific gap between your current profile and an active job.</p>
            <div className="job-divider" />
            <ul className="job-list">
              {roadmaps.slice(0, 3).map((roadmap) => (
                <li className="job-list-item" key={roadmap.id}>
                  <div className="job-list-item-header">
                    <div>
                      <h4 style={{ margin: 0 }}>{roadmap.jobId}</h4>
                      <p style={{ margin: "6px 0 0" }}>{roadmap.totalSteps} steps · {roadmap.completedSteps} completed</p>
                    </div>
                    <span className="job-chip">{roadmap.status}</span>
                  </div>
                  <div className="job-chip-row">
                    {safeList(roadmap.steps).slice(0, 3).map((step) => <span className="job-chip" key={step.id}>{step.skillName}</span>)}
                  </div>
                </li>
              ))}
            </ul>
          </section>

          <aside className="job-card job-sidebar">
            <h3>Signals</h3>
            <p className="job-muted">Recent recommendations are summarized from live backend metrics.</p>
            <div className="job-divider" />
            <ul className="job-bullet-list">
              {safeList(dashboard?.recentActivities).map((activity) => (
                <li key={activity} className="job-list-item">{activity}</li>
              ))}
            </ul>
          </aside>

          <section className="job-card job-section">
            <h3>Placement Insights</h3>
            <p className="job-muted">A structured view of recommendation fit, ATS trend, and skill distribution.</p>
            <div className="job-divider" />
            <ul className="job-list">
              {insights.slice(0, 3).map((item) => (
                <li className="job-list-item" key={item.recommendation?.id}>
                  <div className="job-list-item-header">
                    <div>
                      <h4 style={{ margin: 0 }}>{item.recommendation?.job?.title}</h4>
                      <p style={{ margin: "6px 0 0" }}>{item.recommendation?.job?.company?.name}</p>
                    </div>
                    <span className="job-chip success">{item.recommendation?.matchPercentage ?? 0}%</span>
                  </div>
                  <div className="job-chip-row">
                    {safeList(item.skillGap?.recommendedSkills).slice(0, 3).map((skill) => <span className="job-chip warning" key={skill}>{skill}</span>)}
                  </div>
                </li>
              ))}
            </ul>
          </section>
        </div>
      </section>
    </main>
  );
}

export default StudentJobs;