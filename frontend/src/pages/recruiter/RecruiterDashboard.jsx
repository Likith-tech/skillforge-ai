import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Badge from '../../components/ui/Badge';
import Button from '../../components/ui/Button';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import { listMyJobs } from '../../services/jobService';
import { getMyCompany } from '../../services/companyService';

export default function RecruiterDashboard() {
  const [jobs, setJobs] = useState([]);
  const [hasCompany, setHasCompany] = useState(true);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    listMyJobs()
      .then((data) => setJobs(data))
      .catch(() => setError('Failed to load your jobs.'))
      .finally(() => setLoading(false));

    getMyCompany()
      .then(() => setHasCompany(true))
      .catch(() => setHasCompany(false));
  }, []);

  const openCount = jobs.filter((j) => j.status === 'OPEN').length;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">Recruiter dashboard</h2>
          <p className="text-sm text-slate-500">Track candidates, post openings, and review applications.</p>
        </div>
        <Link to="/recruiter/jobs/new">
          <Button>Post a job</Button>
        </Link>
      </div>

      {!hasCompany && (
        <Alert variant="info">
          You haven&apos;t created a company profile yet.{' '}
          <Link to="/recruiter/company" className="font-semibold underline">
            Set one up
          </Link>{' '}
          so candidates see your company details on every job you post.
        </Alert>
      )}

      {error && <Alert variant="error">{error}</Alert>}

      <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
        <Card className="text-center">
          <p className="text-2xl font-bold text-slate-900">{jobs.length}</p>
          <p className="mt-1 text-xs font-medium uppercase tracking-wide text-slate-400">Total jobs</p>
        </Card>
        <Card className="text-center">
          <p className="text-2xl font-bold text-slate-900">{openCount}</p>
          <p className="mt-1 text-xs font-medium uppercase tracking-wide text-slate-400">Open</p>
        </Card>
      </div>

      <Card
        title="Recent postings"
        action={
          <Link to="/recruiter/jobs" className="text-sm font-semibold text-indigo-600 hover:text-indigo-500">
            Manage all
          </Link>
        }
      >
        {loading ? (
          <div className="flex justify-center py-6">
            <Spinner />
          </div>
        ) : jobs.length === 0 ? (
          <p className="text-sm text-slate-500">You haven&apos;t posted any jobs yet.</p>
        ) : (
          <ul className="divide-y divide-slate-100">
            {jobs.slice(0, 5).map((job) => (
              <li key={job.id} className="flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
                <div className="min-w-0">
                  <p className="font-medium text-slate-900">{job.title}</p>
                  <p className="text-sm text-slate-500">{job.location}</p>
                </div>
                <div className="flex shrink-0 items-center gap-2">
                  <Badge tone={job.status === 'OPEN' ? 'emerald' : 'slate'}>{job.status}</Badge>
                  <Link to={`/recruiter/jobs/${job.id}/applicants`}>
                    <Button variant="secondary">Applicants</Button>
                  </Link>
                </div>
              </li>
            ))}
          </ul>
        )}
      </Card>
    </div>
  );
}
