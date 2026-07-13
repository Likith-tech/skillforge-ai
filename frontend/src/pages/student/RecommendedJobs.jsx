import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Badge from '../../components/ui/Badge';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import JobCard from '../../components/jobs/JobCard';
import { getRecommendedJobs } from '../../services/jobService';
import { getErrorMessage } from '../../utils/getErrorMessage';

export default function RecommendedJobs() {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getRecommendedJobs()
      .then((data) => setMatches(data))
      .catch((err) => setError(getErrorMessage(err, 'Failed to load recommendations.')))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Recommended jobs</h2>
        <p className="text-sm text-slate-500">
          Ranked by how well your current resume&apos;s skills match each job&apos;s requirements.
        </p>
      </div>

      {error && <Alert variant="error">{error}</Alert>}

      {loading ? (
        <div className="flex justify-center py-10">
          <Spinner />
        </div>
      ) : matches.length === 0 ? (
        <Card>
          <p className="text-sm text-slate-600">
            No recommendations yet.{' '}
            <Link to="/student/resume" className="font-semibold text-indigo-600 hover:text-indigo-500">
              Upload a resume
            </Link>{' '}
            so we can match you against open roles.
          </p>
        </Card>
      ) : (
        <div className="space-y-4">
          {matches.map((match) => (
            <div key={match.job.id} className="space-y-2">
              <JobCard job={match.job} to={`/student/jobs/${match.job.id}`} matchScore={match.matchScore} />
              {match.missingSkills?.length > 0 && (
                <div className="flex flex-wrap items-center gap-1.5 pl-1 text-xs text-slate-500">
                  <span>Missing:</span>
                  {match.missingSkills.map((skill) => (
                    <Badge key={skill} tone="rose">
                      {skill}
                    </Badge>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
