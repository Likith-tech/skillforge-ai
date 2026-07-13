import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Topbar from './Topbar';
import MobileNav from './MobileNav';

export default function DashboardLayout({ navItems, title }) {
  return (
    <div className="flex min-h-screen bg-slate-50">
      <Sidebar items={navItems} />
      <div className="flex min-w-0 flex-1 flex-col">
        <Topbar title={title} />
        <MobileNav items={navItems} />
        <main className="flex-1 px-4 py-6 sm:px-6 lg:px-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
