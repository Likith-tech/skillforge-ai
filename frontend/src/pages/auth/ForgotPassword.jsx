import { useState } from "react";
import { Link } from "react-router-dom";
import { requestPasswordReset } from "../../services/authService";

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [resetToken, setResetToken] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");
    setResetToken("");
    setSubmitting(true);

    try {
      const data = await requestPasswordReset(email);
      setMessage("Reset instructions are ready if the email exists.");
      if (data?.resetToken) {
        setResetToken(data.resetToken);
      }
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Unable to request password reset.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main>
      <h1>Forgot Password</h1>

      <form onSubmit={handleSubmit}>
        {error && <div className="form-alert form-alert-error">{error}</div>}
        {message && <div className="form-alert form-alert-success">{message}</div>}
        {resetToken && (
          <div className="form-alert form-alert-success">
            Reset token: <strong>{resetToken}</strong>
          </div>
        )}

        <label>
          <span>Email</span>
          <input
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </label>

        <button type="submit" disabled={submitting}>
          {submitting ? "Sending..." : "Request Reset"}
        </button>
      </form>

      <p>
        Have a reset token? <Link to="/reset-password">Reset password</Link>
      </p>
    </main>
  );
}

export default ForgotPassword;
