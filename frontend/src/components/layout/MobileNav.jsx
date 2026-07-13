import { NavLink } from 'react-router-dom';

export default function MobileNav({ items }) {
  return (
    <nav className="flex gap-1 overflow-x-auto border-b border-slate-200 bg-white px-3 py-2 lg:hidden">
      {items.map((item) => (
        <NavLink
          key={item.to}
          to={item.to}
          end={item.end}
          className={({ isActive }) =>
            `shrink-0 rounded-md px-3 py-1.5 text-sm font-medium ${
              isActive ? 'bg-indigo-50 text-indigo-700' : 'text-slate-600 hover:bg-slate-50'
            }`
          }
        >
          {item.label}
        </NavLink>
      ))}
    </nav>
  );
}
