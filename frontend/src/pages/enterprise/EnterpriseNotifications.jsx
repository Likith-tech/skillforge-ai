import { useEffect, useState } from "react";
import { deleteNotification, getNotifications, markNotificationRead } from "../../services/enterpriseService";
import "./EnterprisePortal.css";

function EnterpriseNotifications() {
  const [items, setItems] = useState([]);

  useEffect(() => {
    const load = async () => setItems(await getNotifications());
    load();
  }, []);

  const markRead = async (notificationId) => {
    await markNotificationRead(notificationId);
    setItems(await getNotifications());
  };

  const remove = async (notificationId) => {
    await deleteNotification(notificationId);
    setItems(await getNotifications());
  };

  return (
    <main className="enterprise-page">
      <section className="enterprise-shell">
        <header className="enterprise-header">
          <div>
            <h1>Notification Center</h1>
            <p>Interview reminders, application updates, offers, and resume-analysis events.</p>
          </div>
        </header>
        <div className="enterprise-grid">
          <section className="enterprise-card enterprise-section">
            <ul className="enterprise-list">
              {items.map((item) => (
                <li className="enterprise-list-item" key={item.id}>
                  <div className="enterprise-actions" style={{ justifyContent: "space-between" }}>
                    <div>
                      <strong>{item.title}</strong>
                      <p>{item.message}</p>
                    </div>
                    <div className="enterprise-actions">
                      <button className="enterprise-button secondary" type="button" onClick={() => markRead(item.id)}>Mark read</button>
                      <button className="enterprise-button" type="button" onClick={() => remove(item.id)}>Delete</button>
                    </div>
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

export default EnterpriseNotifications;