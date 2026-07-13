import Card from '../ui/Card';
import Badge from '../ui/Badge';
import Button from '../ui/Button';

// Static placeholder until GET /jobs exists.
const JOBS = [
  {
    id: 1,
    title: 'Frontend Engineer',
    company: 'Nimbus Cloud',
    location: 'Remote',
    type: 'Full-time',
  },
  {
    id: 2,
    title: 'Data Analyst Intern',
    company: 'Fieldstone Analytics',
    location: 'Bengaluru, IN',
    type: 'Internship',
  },
  {
    id: 3,
    title: 'Backend Engineer (Java)',
    company: 'Harbor Systems',
    location: 'Hyderabad, IN',
    type: 'Full-time',
  },
];

export default function JobListings() {
  return (
    <Card title="Recommended jobs">
      <ul className="divide-y divide-slate-100">
        {JOBS.map((job) => (
          <li key={job.id} className="flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
            <div>
              <p className="font-medium text-slate-900">{job.title}</p>
              <p className="text-sm text-slate-500">
                {job.company} &middot; {job.location}
              </p>
            </div>
            <div className="flex items-center gap-3">
              <Badge tone="slate">{job.type}</Badge>
              <Button variant="secondary">Apply</Button>
            </div>
          </li>
        ))}
      </ul>
    </Card>
  );
}
