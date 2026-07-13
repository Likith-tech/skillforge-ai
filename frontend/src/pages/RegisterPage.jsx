import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthLayout from '../components/layout/AuthLayout';
import Input from '../components/ui/Input';
import Button from '../components/ui/Button';
import Alert from '../components/ui/Alert';
import { useAuth } from '../hooks/useAuth';
import { getErrorMessage } from '../utils/getErrorMessage';
import { getDashboardPathForUser, ROLES } from '../utils/roles';

const SELF_REGISTERABLE_ROLES = [ROLES.STUDENT, ROLES.RECRUITER];

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: ROLES.STUDENT,
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setSubmitting(true);
    try {
      const { confirmPassword: _confirmPassword, ...payload } = form;
      const user = await register(payload);
      navigate(getDashboardPathForUser(user), { replace: true });
    } catch (err) {
      setError(getErrorMessage(err, 'Could not create your account.'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <AuthLayout title="Create your account" subtitle="Join SkillForge AI as a student or recruiter.">
      <form className="space-y-4" onSubmit={handleSubmit}>
        <Alert variant="error">{error}</Alert>

        <Input
          id="fullName"
          name="fullName"
          label="Full name"
          autoComplete="name"
          required
          value={form.fullName}
          onChange={handleChange}
        />
        <Input
          id="email"
          name="email"
          type="email"
          label="Email"
          autoComplete="email"
          required
          value={form.email}
          onChange={handleChange}
        />

        <div>
          <label htmlFor="role" className="mb-1 block text-sm font-medium text-slate-700">
            I am a
          </label>
          <select
            id="role"
            name="role"
            value={form.role}
            onChange={handleChange}
            className="block w-full rounded-md border-0 px-3 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm"
          >
            {SELF_REGISTERABLE_ROLES.map((role) => (
              <option key={role} value={role}>
                {role.charAt(0) + role.slice(1).toLowerCase()}
              </option>
            ))}
          </select>
        </div>

        <Input
          id="password"
          name="password"
          type="password"
          label="Password"
          autoComplete="new-password"
          minLength={8}
          required
          value={form.password}
          onChange={handleChange}
        />
        <Input
          id="confirmPassword"
          name="confirmPassword"
          type="password"
          label="Confirm password"
          autoComplete="new-password"
          minLength={8}
          required
          value={form.confirmPassword}
          onChange={handleChange}
        />

        <Button type="submit" className="w-full" loading={submitting}>
          Create account
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-slate-500">
        Already have an account?{' '}
        <Link to="/login" className="font-semibold text-indigo-600 hover:text-indigo-500">
          Sign in
        </Link>
      </p>
    </AuthLayout>
  );
}
