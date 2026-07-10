import { useEffect, useState } from "react";
import { getAdminDashboard, getAuditLogs, getFiles, getNotifications, getUnreadNotificationCount, searchGlobal } from "../../services/enterpriseService";
import "./EnterprisePortal.css";

const safe = (value) => (Array.isArray(value) ? value : []);

function EnterpriseAdminDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [auditLogs, setAuditLogs] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [files, setFiles] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [query, setQuery] = useState("");
  const [searchResult, setSearchResult] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      const [dashboardData, auditData, notificationData, unreadData, fileData] = await Promise.all([
        getAdminDashboard(),
        getAuditLogs(),
        getNotifications(),
        getUnreadNotificationCount(),
        getFiles(),
      ]);
      setDashboard(dashboardData);
      setAuditLogs(safe(auditData));
      setNotifications(safe(notificationData));
      setUnreadCount(unreadData);
      setFiles(safe(fileData));
      setLoading(false);
    };

    load();
  }, []);

  const runSearch = async (event) => {
    event.preventDefault();
    setSearchResult(await searchGlobal(query));
  };

  if (loading) {
    return <main className="enterprise-page">Loading admin dashboard...</main>;
  }

  return (
    <main className="enterprise-page">
      <section className="enterprise-shell">
        <header className="enterprise-header">
          <div>
            <h1>Admin Control Center</h1>
            <p>Enterprise visibility into users, jobs, placement analytics, notifications, and system activity.</p>
          </div>
          <div className="enterprise-actions">
            <span className="enterprise-chip success">Unread notifications {unreadCount}</span>
          </div>
        </header>

        <div className="enterprise-grid">
          {(dashboard?.cards || []).slice(0, 8).map((card) => (
            <article className="enterprise-card enterprise-stat" key={card.label}>
              <span>{card.label}</span>
              <strong>{card.value}</strong>
            </article>
          ))}

          <section className="enterprise-card enterprise-section">
            <h2>Global Search</h2>
            <form onSubmit={runSearch} className="enterprise-actions" style={{ marginTop: 12 }}>
              <input className="enterprise-input" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search students, jobs, recruiters, companies, skills" />
              <button className="enterprise-button" type="submit">Search</button>
            </form>
            {searchResult && (
              <div className="enterprise-grid" style={{ padding: 0, marginTop: 18 }}>
                <article className="enterprise-card enterprise-section">
                  <h3>Students</h3>
                  <div className="enterprise-chip-row">{safe(searchResult.students).map((item) => <span className="enterprise-chip" key={item.id}>{item.name}</span>)}</div>
                </article>
                <article className="enterprise-card enterprise-sidebar">
                  <h3>Jobs</h3>
                  <div className="enterprise-chip-row">{safe(searchResult.jobs).map((item) => <span className="enterprise-chip" key={item.id}>{item.title}</span>)}</div>
                </article>
              </div>
            )}
          </section>

          <aside className="enterprise-card enterprise-sidebar">
            <h3>Highlights</h3>
            <ul className="enterprise-list">
              {safe(dashboard?.highlights).map((item) => <li className="enterprise-list-item" key={item}>{item}</li>)}
            </ul>
          </aside>

          <section className="enterprise-card enterprise-section">
            <h2>Notifications</h2>
            <ul className="enterprise-list">
              {notifications.slice(0, 6).map((item) => <li className="enterprise-list-item" key={item.id}>{item.title} · {item.status}</li>)}
            </ul>
          </section>

          <aside className="enterprise-card enterprise-sidebar">
            <h3>Files</h3>
            <ul className="enterprise-list">
              {files.slice(0, 6).map((item) => <li className="enterprise-list-item" key={item.id}>{item.assetType}: {item.originalFileName}</li>)}
            </ul>
          </aside>

          <section className="enterprise-card enterprise-section">
            <h2>Audit Logs</h2>
            <ul className="enterprise-list">
              {auditLogs.slice(0, 8).map((item) => <li className="enterprise-list-item" key={item.id}>{item.action} · {item.entityType} · {item.createdAt}</li>)}
            </ul>
          </section>
        </div>
      </section>
    </main>
  );
}

export default EnterpriseAdminDashboard;