import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { resetPassword } from "../../services/authService";

const initialForm = {
  resetToken: "",
  newPassword: "",
  confirmPassword: "",
};

function ResetPassword() {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (form.newPassword !== form.confirmPassword) {
      setError("New password and confirm password must match.");
      return;
    }

    setSubmitting(true);

    try {
      await resetPassword(form);
      navigate("/login");
    } catch (resetError) {
      setError(resetError.response?.data?.message || "Unable to reset password.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main>
      <h1>Reset Password</h1>

      <form onSubmit={handleSubmit}>
        {error && <div className="form-alert form-alert-error">{error}</div>}

        <label>
          <span>Reset Token</span>
          <input name="resetToken" value={form.resetToken} onChange={handleChange} required />
        </label>

        <label>
          <span>New Password</span>
          <input
            name="newPassword"
            type="password"
            value={form.newPassword}
            onChange={handleChange}
            minLength={6}
            required
          />
        </label>

        <label>
          <span>Confirm Password</span>
          <input
            name="confirmPassword"
            type="password"
            value={form.confirmPassword}
            onChange={handleChange}
            minLength={6}
            required
          />
        </label>

        <button type="submit" disabled={submitting}>
          {submitting ? "Resetting..." : "Reset Password"}
        </button>
      </form>

      <p>
        Back to <Link to="/login">login</Link>
      </p>
    </main>
  );
}

export default ResetPassword;
