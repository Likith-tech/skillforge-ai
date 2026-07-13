import Card from '../ui/Card';
import Badge from '../ui/Badge';
import { useAuth } from '../../hooks/useAuth';

export default function ProfileCard() {
  const { user } = useAuth();

  return (
    <Card title="Profile">
      <div className="flex items-center gap-4">
        <div className="flex h-14 w-14 items-center justify-center rounded-full bg-indigo-600 text-xl font-semibold text-white">
          {user?.fullName?.charAt(0)?.toUpperCase() ?? '?'}
        </div>
        <div>
          <p className="font-semibold text-slate-900">{user?.fullName}</p>
          <p className="text-sm text-slate-500">{user?.email}</p>
          <div className="mt-1 flex gap-1">
            {user?.roles?.map((role) => (
              <Badge key={role} tone="indigo">
                {role}
              </Badge>
            ))}
          </div>
        </div>
      </div>
    </Card>
  );
}
