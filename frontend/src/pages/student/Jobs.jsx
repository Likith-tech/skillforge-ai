import { useCallback, useEffect, useState } from 'react';
import Card from '../../components/ui/Card';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import Pagination from '../../components/ui/Pagination';
import SortSelect from '../../components/ui/SortSelect';
import SearchBar from '../../components/jobs/SearchBar';
import FilterPanel from '../../components/jobs/FilterPanel';
import JobCard from '../../components/jobs/JobCard';
import { listJobs, saveJob, unsaveJob, listSavedJobs, JOB_SORT_OPTIONS } from '../../services/jobService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const EMPTY_FILTERS = { location: '', type: '', experienceLevel: '', skill: '', company: '' };
const PAGE_SIZE = 10;

export default function Jobs() {
  const [keyword, setKeyword] = useState('');
  const [filters, setFilters] = useState(EMPTY_FILTERS);
  const [sort, setSort] = useState('newest');
  const [page, setPage] = useState(0);
  const [jobsPage, setJobsPage] = useState({ content: [], page: 0, totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [savedIds, setSavedIds] = useState(new Set());

  const refreshJobs = useCallback(() => {
    setLoading(true);
    setError(null);
    listJobs({ keyword, ...filters, sort, page, size: PAGE_SIZE })
      .then((data) => setJobsPage(data))
      .catch((err) => setError(getErrorMessage(err, 'Failed to load jobs.')))
      .finally(() => setLoading(false));
  }, [keyword, filters, sort, page]);

  useEffect(() => {
    const timeout = setTimeout(refreshJobs, 300);
    return () => clearTimeout(timeout);
  }, [refreshJobs]);

  // Any change to the search/filter/sort criteria invalidates the current page.
  useEffect(() => {
    setPage(0);
  }, [keyword, filters, sort]);

  const jobs = jobsPage.content;

  useEffect(() => {
    listSavedJobs()
      .then((data) => setSavedIds(new Set(data.map((s) => s.job.id))))
      .catch(() => setSavedIds(new Set()));
  }, []);

  function handleFilterChange(field, value) {
    setFilters((prev) => ({ ...prev, [field]: value }));
  }

  async function handleToggleSave(jobId) {
    const isSaved = savedIds.has(jobId);
    try {
      if (isSaved) {
        await unsaveJob(jobId);
        setSavedIds((prev) => {
          const next = new Set(prev);
          next.delete(jobId);
          return next;
        });
      } else {
        await saveJob(jobId);
        setSavedIds((prev) => new Set(prev).add(jobId));
      }
    } catch {
      // Non-critical UI action - silently ignore, the star just won't toggle.
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Browse jobs</h2>
        <p className="text-sm text-slate-500">Search and filter open roles across every recruiter on SkillForge AI.</p>
      </div>

      <Card>
        <div className="space-y-4">
          <SearchBar value={keyword} onChange={setKeyword} />
          <FilterPanel filters={filters} onChange={handleFilterChange} />
          <div className="flex justify-end">
            <SortSelect id="jobs-sort" value={sort} onChange={setSort} options={JOB_SORT_OPTIONS} />
          </div>
        </div>
      </Card>

      {error && <Alert variant="error">{error}</Alert>}

      {loading ? (
        <div className="flex justify-center py-10">
          <Spinner />
        </div>
      ) : jobs.length === 0 ? (
        <Card>
          <p className="text-sm text-slate-500">No jobs match your search yet. Try broadening your filters.</p>
        </Card>
      ) : (
        <Card>
          <div className="space-y-4">
            {jobs.map((job) => (
              <JobCard
                key={job.id}
                job={job}
                to={`/student/jobs/${job.id}`}
                saved={savedIds.has(job.id)}
                onToggleSave={() => handleToggleSave(job.id)}
              />
            ))}
          </div>
          <Pagination
            page={jobsPage.page}
            totalPages={jobsPage.totalPages}
            totalElements={jobsPage.totalElements}
            onPageChange={setPage}
          />
        </Card>
      )}
    </div>
  );
}
