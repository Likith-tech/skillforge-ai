import Card from '../ui/Card';
import Badge from '../ui/Badge';

const STATS = [
  { label: 'Total users', value: 4 },
  { label: 'Students', value: 2 },
  { label: 'Recruiters', value: 1 },
  { label: 'Admins', value: 1 },
];

export default function AdminControls() {
  return (
    <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
      {STATS.map((stat) => (
        <Card key={stat.label} className="text-center">
          <p className="text-2xl font-bold text-slate-900">{stat.value}</p>
          <p className="mt-1 text-xs font-medium uppercase tracking-wide text-slate-400">{stat.label}</p>
        </Card>
      ))}
      <Card className="col-span-2 sm:col-span-4" title="System status">
        <div className="flex items-center gap-2">
          <span className="h-2.5 w-2.5 rounded-full bg-emerald-500" />
          <span className="text-sm text-slate-600">All systems operational</span>
          <Badge tone="emerald">API healthy</Badge>
        </div>
      </Card>
    </div>
  );
}
