import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import Spinner from '../components/ui/Spinner';
import { getDashboardPathForUser } from '../utils/roles';

/**
 * Keeps already-authenticated users out of /login and /register by
 * bouncing them straight to their role's dashboard.
 */
export default function PublicOnlyRoute() {
  const { user, loading, isAuthenticated } = useAuth();

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Spinner />
      </div>
    );
  }

  if (isAuthenticated) {
    return <Navigate to={getDashboardPathForUser(user)} replace />;
  }

  return <Outlet />;
}
