import { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import Pagination from '../../components/ui/Pagination';
import SortSelect from '../../components/ui/SortSelect';
import CandidateCard from '../../components/jobs/CandidateCard';
import {
  listApplicationsForJob,
  updateApplicationStatus,
  APPLICATION_SORT_OPTIONS,
  APPLICATION_STATUSES,
} from '../../services/applicationService';
import { getJob } from '../../services/jobService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const PAGE_SIZE = 10;
const STATUS_FILTER_OPTIONS = [{ value: '', label: 'All statuses' }, ...APPLICATION_STATUSES];

export default function Applicants() {
  const { jobId } = useParams();

  const [job, setJob] = useState(null);
  const [status, setStatus] = useState('');
  const [sort, setSort] = useState('newest');
  const [page, setPage] = useState(0);
  const [appsPage, setAppsPage] = useState({ content: [], page: 0, totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [updatingId, setUpdatingId] = useState(null);

  const refresh = useCallback(() => {
    setLoading(true);
    setError(null);
    Promise.all([getJob(jobId), listApplicationsForJob(jobId, { status, sort, page, size: PAGE_SIZE })])
      .then(([jobData, applicationsData]) => {
        setJob(jobData);
        setAppsPage(applicationsData);
      })
      .catch((err) => setError(getErrorMessage(err, 'Failed to load applicants.')))
      .finally(() => setLoading(false));
  }, [jobId, status, sort, page]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  useEffect(() => {
    setPage(0);
  }, [status, sort]);

  const applications = appsPage.content;

  async function handleStatusChange(applicationId, newStatus) {
    setUpdatingId(applicationId);
    setError(null);
    try {
      const updated = await updateApplicationStatus(applicationId, newStatus);
      setAppsPage((prev) => ({
        ...prev,
        content: prev.content.map((app) => (app.id === applicationId ? updated : app)),
      }));
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update application status.'));
    } finally {
      setUpdatingId(null);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">Applicants{job ? ` - ${job.title}` : ''}</h2>
          <p className="text-sm text-slate-500">Review candidates, their resumes, and ATS scores.</p>
        </div>
        <div className="flex gap-3">
          <SortSelect id="applicants-status" label="Status" value={status} onChange={setStatus} options={STATUS_FILTER_OPTIONS} />
          <SortSelect id="applicants-sort" value={sort} onChange={setSort} options={APPLICATION_SORT_OPTIONS} />
        </div>
      </div>

      {error && <Alert variant="error">{error}</Alert>}

      {loading ? (
        <div className="flex justify-center py-10">
          <Spinner />
        </div>
      ) : applications.length === 0 ? (
        <Card>
          <p className="text-sm text-slate-500">No applications match this filter.</p>
        </Card>
      ) : (
        <div className="space-y-4">
          {applications.map((application) => (
            <CandidateCard
              key={application.id}
              application={application}
              onStatusChange={handleStatusChange}
              updating={updatingId === application.id}
            />
          ))}
          <Pagination
            page={appsPage.page}
            totalPages={appsPage.totalPages}
            totalElements={appsPage.totalElements}
            onPageChange={setPage}
          />
        </div>
      )}
    </div>
  );
}
