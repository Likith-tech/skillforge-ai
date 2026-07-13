import AdminControls from '../../components/admin/AdminControls';
import UserTable from '../../components/admin/UserTable';

export default function AdminDashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Admin control panel</h2>
        <p className="text-sm text-slate-500">Platform-wide overview and user management.</p>
      </div>

      <AdminControls />
      <UserTable />
    </div>
  );
}
