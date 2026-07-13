import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../ui/Card';
import Badge from '../ui/Badge';
import Spinner from '../ui/Spinner';
import { getRecommendedJobs } from '../../services/jobService';

export default function RecommendedJobsWidget() {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getRecommendedJobs()
      .then((data) => setMatches(data.slice(0, 5)))
      .catch(() => setMatches([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Card
      title="Recommended jobs"
      action={
        <Link to="/student/jobs/recommended" className="text-sm font-semibold text-indigo-600 hover:text-indigo-500">
          View all
        </Link>
      }
    >
      {loading ? (
        <div className="flex justify-center py-6">
          <Spinner />
        </div>
      ) : matches.length === 0 ? (
        <p className="text-sm text-slate-500">
          No recommendations yet.{' '}
          <Link to="/student/resume" className="font-semibold text-indigo-600 hover:text-indigo-500">
            Upload a resume
          </Link>{' '}
          to get matched against open roles.
        </p>
      ) : (
        <ul className="divide-y divide-slate-100">
          {matches.map((match) => (
            <li key={match.job.id} className="flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
              <div className="min-w-0">
                <Link
                  to={`/student/jobs/${match.job.id}`}
                  className="font-medium text-slate-900 hover:text-indigo-600"
                >
                  {match.job.title}
                </Link>
                <p className="text-sm text-slate-500">
                  {match.job.company}
                  {match.job.location && <> &middot; {match.job.location}</>}
                </p>
              </div>
              <Badge tone="amber">{match.matchScore}% match</Badge>
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
}
