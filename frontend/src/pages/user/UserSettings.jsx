import { useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext.jsx";
import { changePassword, getUserSettings, updateUserSettings } from "../../services/userService";
import "../student/StudentProfile.css";

const initialSettings = {
  name: "",
  email: "",
  emailNotificationsEnabled: true,
  inAppNotificationsEnabled: true,
  themePreference: "SYSTEM",
};

const initialPassword = {
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
};

function UserSettings() {
  const { updateUser } = useAuth();
  const [settings, setSettings] = useState(initialSettings);
  const [passwordForm, setPasswordForm] = useState(initialPassword);
  const [loading, setLoading] = useState(true);
  const [savingSettings, setSavingSettings] = useState(false);
  const [savingPassword, setSavingPassword] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const loadSettings = async () => {
      try {
        const data = await getUserSettings();
        setSettings({
          name: data.name || "",
          email: data.email || "",
          emailNotificationsEnabled: Boolean(data.emailNotificationsEnabled),
          inAppNotificationsEnabled: Boolean(data.inAppNotificationsEnabled),
          themePreference: data.themePreference || "SYSTEM",
        });
      } catch (loadError) {
        setError("Unable to load settings.");
      } finally {
        setLoading(false);
      }
    };

    loadSettings();
  }, []);

  const handleSettingsChange = (event) => {
    const { name, type, checked, value } = event.target;
    setSettings((current) => ({ ...current, [name]: type === "checkbox" ? checked : value }));
  };

  const handlePasswordChange = (event) => {
    const { name, value } = event.target;
    setPasswordForm((current) => ({ ...current, [name]: value }));
  };

  const handleSettingsSubmit = async (event) => {
    event.preventDefault();
    setSavingSettings(true);
    setMessage("");
    setError("");

    try {
      const updated = await updateUserSettings(settings);
      setSettings(updated);
      updateUser({
        id: updated.id,
        name: updated.name,
        email: updated.email,
        role: updated.role,
      });
      setMessage("Settings saved successfully.");
    } catch (saveError) {
      setError(saveError.response?.data?.message || "Unable to save settings.");
    } finally {
      setSavingSettings(false);
    }
  };

  const handlePasswordSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setError("New password and confirm password must match.");
      return;
    }

    setSavingPassword(true);

    try {
      await changePassword(passwordForm);
      setPasswordForm(initialPassword);
      setMessage("Password changed successfully.");
    } catch (passwordError) {
      setError(passwordError.response?.data?.message || "Unable to change password.");
    } finally {
      setSavingPassword(false);
    }
  };

  if (loading) {
    return <div className="student-profile-page">Loading settings...</div>;
  }

  return (
    <main className="student-profile-page">
      <section className="student-profile-header">
        <h1>User Settings</h1>
        <p>Manage your account details and preferences.</p>
      </section>

      {error && <div className="form-alert form-alert-error">{error}</div>}
      {message && <div className="form-alert form-alert-success">{message}</div>}

      <form className="student-profile-form" onSubmit={handleSettingsSubmit}>
        <div className="profile-form-grid">
          <label className="profile-field">
            <span>Name</span>
            <input name="name" value={settings.name} onChange={handleSettingsChange} required />
          </label>

          <label className="profile-field">
            <span>Email</span>
            <input
              name="email"
              type="email"
              value={settings.email}
              onChange={handleSettingsChange}
              required
            />
          </label>

          <label className="profile-field">
            <span>Theme</span>
            <select name="themePreference" value={settings.themePreference} onChange={handleSettingsChange}>
              <option value="SYSTEM">System</option>
              <option value="LIGHT">Light</option>
              <option value="DARK">Dark</option>
            </select>
          </label>
        </div>

        <label className="profile-field">
          <span>
            <input
              name="emailNotificationsEnabled"
              type="checkbox"
              checked={settings.emailNotificationsEnabled}
              onChange={handleSettingsChange}
            />{" "}
            Email notifications
          </span>
        </label>

        <label className="profile-field">
          <span>
            <input
              name="inAppNotificationsEnabled"
              type="checkbox"
              checked={settings.inAppNotificationsEnabled}
              onChange={handleSettingsChange}
            />{" "}
            In-app notifications
          </span>
        </label>

        <button type="submit" disabled={savingSettings}>
          {savingSettings ? "Saving..." : "Save Settings"}
        </button>
      </form>

      <form className="student-profile-form" onSubmit={handlePasswordSubmit}>
        <h2>Change Password</h2>

        <div className="profile-form-grid">
          <label className="profile-field">
            <span>Current Password</span>
            <input
              name="currentPassword"
              type="password"
              value={passwordForm.currentPassword}
              onChange={handlePasswordChange}
              required
            />
          </label>

          <label className="profile-field">
            <span>New Password</span>
            <input
              name="newPassword"
              type="password"
              value={passwordForm.newPassword}
              onChange={handlePasswordChange}
              minLength={6}
              required
            />
          </label>

          <label className="profile-field">
            <span>Confirm Password</span>
            <input
              name="confirmPassword"
              type="password"
              value={passwordForm.confirmPassword}
              onChange={handlePasswordChange}
              minLength={6}
              required
            />
          </label>
        </div>

        <button type="submit" disabled={savingPassword}>
          {savingPassword ? "Changing..." : "Change Password"}
        </button>
      </form>
    </main>
  );
}

export default UserSettings;
