import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Badge from '../../components/ui/Badge';
import Button from '../../components/ui/Button';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import Pagination from '../../components/ui/Pagination';
import { listMyJobs, deleteJob, setJobStatus } from '../../services/jobService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const PAGE_SIZE = 10;

export default function ManageJobs() {
  const [page, setPage] = useState(0);
  const [jobsPage, setJobsPage] = useState({ content: [], page: 0, totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busyId, setBusyId] = useState(null);

  const refresh = useCallback(() => {
    setLoading(true);
    setError(null);
    listMyJobs({ page, size: PAGE_SIZE, sort: 'newest' })
      .then((data) => setJobsPage(data))
      .catch((err) => setError(getErrorMessage(err, 'Failed to load your jobs.')))
      .finally(() => setLoading(false));
  }, [page]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const jobs = jobsPage.content;

  async function handleToggleStatus(job) {
    setBusyId(job.id);
    setError(null);
    try {
      await setJobStatus(job.id, job.status === 'OPEN' ? 'CLOSED' : 'OPEN');
      refresh();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update job status.'));
    } finally {
      setBusyId(null);
    }
  }

  async function handleDelete(job) {
    if (!window.confirm(`Delete "${job.title}"? This can't be undone.`)) return;
    setBusyId(job.id);
    setError(null);
    try {
      await deleteJob(job.id);
      refresh();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to delete this job.'));
    } finally {
      setBusyId(null);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">Manage jobs</h2>
          <p className="text-sm text-slate-500">Edit, activate/deactivate, or remove your postings.</p>
        </div>
        <Link to="/recruiter/jobs/new">
          <Button>Post a job</Button>
        </Link>
      </div>

      {error && <Alert variant="error">{error}</Alert>}

      {loading ? (
        <div className="flex justify-center py-10">
          <Spinner />
        </div>
      ) : jobs.length === 0 ? (
        <Card>
          <p className="text-sm text-slate-500">You haven&apos;t posted any jobs yet.</p>
        </Card>
      ) : (
        <div className="space-y-4">
          {jobs.map((job) => (
            <Card key={job.id}>
              <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
                <div className="min-w-0">
                  <div className="flex items-center gap-2">
                    <p className="font-medium text-slate-900">{job.title}</p>
                    <Badge tone={job.status === 'OPEN' ? 'emerald' : 'slate'}>{job.status}</Badge>
                  </div>
                  <p className="text-sm text-slate-500">
                    {job.location} &middot; Posted {new Date(job.createdAt).toLocaleDateString()}
                  </p>
                </div>
                <div className="flex shrink-0 flex-wrap items-center gap-2">
                  <Link to={`/recruiter/jobs/${job.id}/applicants`}>
                    <Button variant="secondary">Applicants</Button>
                  </Link>
                  <Link to={`/recruiter/jobs/${job.id}/edit`}>
                    <Button variant="secondary">Edit</Button>
                  </Link>
                  <Button
                    variant="secondary"
                    onClick={() => handleToggleStatus(job)}
                    loading={busyId === job.id}
                  >
                    {job.status === 'OPEN' ? 'Deactivate' : 'Activate'}
                  </Button>
                  <Button variant="danger" onClick={() => handleDelete(job)} loading={busyId === job.id}>
                    Delete
                  </Button>
                </div>
              </div>
            </Card>
          ))}
          <Pagination
            page={jobsPage.page}
            totalPages={jobsPage.totalPages}
            totalElements={jobsPage.totalElements}
            onPageChange={setPage}
          />
        </div>
      )}
    </div>
  );
}
