import { Link } from 'react-router-dom';
import Card from '../ui/Card';
import Badge from '../ui/Badge';
import { formatJobType, formatExperienceLevel } from '../../services/jobService';

export default function JobCard({ job, to, matchScore, saved, onToggleSave, actions }) {
  const title = to ? (
    <Link to={to} className="font-semibold text-slate-900 hover:text-indigo-600">
      {job.title}
    </Link>
  ) : (
    <p className="font-semibold text-slate-900">{job.title}</p>
  );

  return (
    <Card>
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          {title}
          <p className="text-sm text-slate-500">
            {job.company}
            {job.location && <> &middot; {job.location}</>}
          </p>
          <div className="mt-2 flex flex-wrap gap-1.5">
            <Badge tone="slate">{formatJobType(job.type)}</Badge>
            {job.experienceLevel && <Badge tone="indigo">{formatExperienceLevel(job.experienceLevel)}</Badge>}
            {job.salaryRange && <Badge tone="emerald">{job.salaryRange}</Badge>}
            {typeof matchScore === 'number' && <Badge tone="amber">{matchScore}% match</Badge>}
          </div>
        </div>
        <div className="flex shrink-0 flex-col items-end gap-2">
          {onToggleSave && (
            <button
              type="button"
              onClick={onToggleSave}
              aria-label={saved ? 'Remove from saved jobs' : 'Save job'}
              className={`text-xl leading-none ${saved ? 'text-amber-500' : 'text-slate-300 hover:text-slate-400'}`}
            >
              {saved ? '★' : '☆'}
            </button>
          )}
          {actions}
        </div>
      </div>
    </Card>
  );
}
