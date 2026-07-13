import Card from '../ui/Card';
import Badge from '../ui/Badge';

// Dummy data until GET /recruiter/applications exists.
const APPLICATIONS = [
  { id: 1, candidate: 'Priya Sharma', job: 'Frontend Engineer', appliedOn: '2026-06-28', status: 'Under review' },
  { id: 2, candidate: 'Arjun Mehta', job: 'Backend Engineer', appliedOn: '2026-07-01', status: 'New' },
  { id: 3, candidate: 'Sara Iqbal', job: 'Data Analyst', appliedOn: '2026-07-03', status: 'Interview scheduled' },
];

const STATUS_TONE = {
  New: 'slate',
  'Under review': 'amber',
  'Interview scheduled': 'indigo',
};

export default function ApplicationList() {
  return (
    <Card title="Recent applications">
      <ul className="divide-y divide-slate-100">
        {APPLICATIONS.map((application) => (
          <li key={application.id} className="flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
            <div>
              <p className="font-medium text-slate-900">{application.candidate}</p>
              <p className="text-sm text-slate-500">
                {application.job} &middot; Applied {application.appliedOn}
              </p>
            </div>
            <Badge tone={STATUS_TONE[application.status]}>{application.status}</Badge>
          </li>
        ))}
      </ul>
    </Card>
  );
}
