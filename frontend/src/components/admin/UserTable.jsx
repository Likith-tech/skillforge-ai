import { useEffect, useState } from 'react';
import Card from '../ui/Card';
import Badge from '../ui/Badge';
import Spinner from '../ui/Spinner';
import { fetchUsers } from '../../services/adminService';

const ROLE_TONE = { STUDENT: 'indigo', RECRUITER: 'amber', ADMIN: 'rose' };

export default function UserTable() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    fetchUsers().then((data) => {
      if (active) {
        setUsers(data);
        setLoading(false);
      }
    });
    return () => {
      active = false;
    };
  }, []);

  return (
    <Card title="All users">
      {loading ? (
        <div className="flex justify-center py-8">
          <Spinner />
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-100 text-sm">
            <thead>
              <tr className="text-left text-xs font-semibold uppercase tracking-wide text-slate-400">
                <th className="py-2 pr-4">Name</th>
                <th className="py-2 pr-4">Email</th>
                <th className="py-2 pr-4">Role</th>
                <th className="py-2 pr-4">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {users.map((user) => (
                <tr key={user.id}>
                  <td className="py-3 pr-4 font-medium text-slate-900">{user.fullName}</td>
                  <td className="py-3 pr-4 text-slate-600">{user.email}</td>
                  <td className="py-3 pr-4">
                    {user.roles.map((role) => (
                      <Badge key={role} tone={ROLE_TONE[role]}>
                        {role}
                      </Badge>
                    ))}
                  </td>
                  <td className="py-3 pr-4">
                    <Badge tone={user.enabled ? 'emerald' : 'slate'}>
                      {user.enabled ? 'Active' : 'Disabled'}
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </Card>
  );
}
