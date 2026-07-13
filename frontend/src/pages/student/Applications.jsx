import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Badge from '../../components/ui/Badge';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import Pagination from '../../components/ui/Pagination';
import SortSelect from '../../components/ui/SortSelect';
import ApplicationStatusBadge from '../../components/jobs/ApplicationStatusBadge';
import { listMyApplications, APPLICATION_SORT_OPTIONS } from '../../services/applicationService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const PAGE_SIZE = 10;

export default function Applications() {
  const [sort, setSort] = useState('newest');
  const [page, setPage] = useState(0);
  const [appsPage, setAppsPage] = useState({ content: [], page: 0, totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const refresh = useCallback(() => {
    setLoading(true);
    listMyApplications({ sort, page, size: PAGE_SIZE })
      .then((data) => setAppsPage(data))
      .catch((err) => setError(getErrorMessage(err, 'Failed to load your applications.')))
      .finally(() => setLoading(false));
  }, [sort, page]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  useEffect(() => {
    setPage(0);
  }, [sort]);

  const applications = appsPage.content;

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">My applications</h2>
          <p className="text-sm text-slate-500">Track the status of every job you've applied to.</p>
        </div>
        <SortSelect id="applications-sort" value={sort} onChange={setSort} options={APPLICATION_SORT_OPTIONS} />
      </div>

      {error && <Alert variant="error">{error}</Alert>}

      {loading ? (
        <div className="flex justify-center py-10">
          <Spinner />
        </div>
      ) : applications.length === 0 ? (
        <Card>
          <p className="text-sm text-slate-600">
            You haven&apos;t applied to any jobs yet.{' '}
            <Link to="/student/jobs" className="font-semibold text-indigo-600 hover:text-indigo-500">
              Browse open roles
            </Link>
            .
          </p>
        </Card>
      ) : (
        <Card>
          <ul className="divide-y divide-slate-100">
            {applications.map((app) => (
              <li key={app.id} className="flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
                <div className="min-w-0">
                  <p className="font-medium text-slate-900">{app.jobTitle}</p>
                  <p className="text-sm text-slate-500">
                    {app.company} &middot; Applied {new Date(app.appliedAt).toLocaleDateString()}
                  </p>
                  {app.status === 'INTERVIEWING' && (
                    <p className="mt-1 text-xs font-medium text-indigo-600">
                      You&apos;ve been invited to interview - watch for a message from the recruiter.
                    </p>
                  )}
                </div>
                <div className="flex shrink-0 items-center gap-2">
                  <Badge tone="amber">{app.matchScore}% match</Badge>
                  <ApplicationStatusBadge status={app.status} />
                </div>
              </li>
            ))}
          </ul>
          <Pagination
            page={appsPage.page}
            totalPages={appsPage.totalPages}
            totalElements={appsPage.totalElements}
            onPageChange={setPage}
          />
        </Card>
      )}
    </div>
  );
}
