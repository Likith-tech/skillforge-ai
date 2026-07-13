import { useCallback, useEffect, useState } from 'react';
import Card from '../../components/ui/Card';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import JobCard from '../../components/jobs/JobCard';
import { listSavedJobs, unsaveJob } from '../../services/jobService';
import { getErrorMessage } from '../../utils/getErrorMessage';

export default function SavedJobs() {
  const [savedJobs, setSavedJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const refresh = useCallback(() => {
    setLoading(true);
    setError(null);
    listSavedJobs()
      .then((data) => setSavedJobs(data))
      .catch((err) => setError(getErrorMessage(err, 'Failed to load saved jobs.')))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  async function handleRemove(jobId) {
    try {
      await unsaveJob(jobId);
      refresh();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to remove this job.'));
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Saved jobs</h2>
        <p className="text-sm text-slate-500">Roles you've bookmarked to revisit later.</p>
      </div>

      {error && <Alert variant="error">{error}</Alert>}

      {loading ? (
        <div className="flex justify-center py-10">
          <Spinner />
        </div>
      ) : savedJobs.length === 0 ? (
        <Card>
          <p className="text-sm text-slate-500">You haven&apos;t saved any jobs yet.</p>
        </Card>
      ) : (
        <div className="space-y-4">
          {savedJobs.map((saved) => (
            <JobCard
              key={saved.id}
              job={saved.job}
              to={`/student/jobs/${saved.job.id}`}
              saved
              onToggleSave={() => handleRemove(saved.job.id)}
            />
          ))}
        </div>
      )}
    </div>
  );
}
