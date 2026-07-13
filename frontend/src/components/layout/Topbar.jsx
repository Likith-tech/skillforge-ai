import { useAuth } from '../../hooks/useAuth';
import Button from '../ui/Button';

export default function Topbar({ title }) {
  const { user, logout } = useAuth();

  return (
    <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-4 sm:px-6">
      <h1 className="text-lg font-semibold text-slate-900">{title}</h1>
      <div className="flex items-center gap-4">
        <div className="text-right leading-tight">
          <p className="text-sm font-medium text-slate-900">{user?.fullName}</p>
          <p className="text-xs text-slate-500">{user?.email}</p>
        </div>
        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-indigo-600 text-sm font-semibold text-white">
          {user?.fullName?.charAt(0)?.toUpperCase() ?? '?'}
        </div>
        <Button variant="secondary" onClick={logout}>
          Log out
        </Button>
      </div>
    </header>
  );
}
