import { Link } from 'react-router-dom';
import Button from '../components/ui/Button';
import { useAuth } from '../hooks/useAuth';
import { getDashboardPathForUser } from '../utils/roles';

export default function UnauthorizedPage() {
  const { user } = useAuth();

  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3 bg-slate-50 px-4 text-center">
      <p className="text-sm font-semibold text-rose-600">403</p>
      <h1 className="text-2xl font-bold text-slate-900">Access denied</h1>
      <p className="text-sm text-slate-500">Your account doesn&apos;t have permission to view that page.</p>
      <Link to={getDashboardPathForUser(user)}>
        <Button className="mt-2">Back to my dashboard</Button>
      </Link>
    </div>
  );
}
