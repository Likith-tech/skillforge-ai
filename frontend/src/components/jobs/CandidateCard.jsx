import { useState } from 'react';
import Card from '../ui/Card';
import Button from '../ui/Button';
import Badge from '../ui/Badge';
import ApplicationStatusBadge from './ApplicationStatusBadge';
import ResumeFilePreviewModal from '../student/ResumeFilePreviewModal';
import { APPLICATION_STATUSES } from '../../services/applicationService';

const selectClass =
  'rounded-md border-0 px-2 py-1.5 text-sm text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600';

export default function CandidateCard({ application, onStatusChange, updating }) {
  const [previewing, setPreviewing] = useState(false);

  return (
    <Card>
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="min-w-0">
          <div className="flex items-center gap-2">
            <p className="font-medium text-slate-900">{application.studentName}</p>
            <ApplicationStatusBadge status={application.status} />
          </div>
          <p className="text-sm text-slate-500">{application.studentEmail}</p>
          <div className="mt-1.5 flex flex-wrap items-center gap-2">
            <Badge tone="amber">{application.matchScore}% job match</Badge>
            {application.atsScore != null && <Badge tone="indigo">ATS score {application.atsScore}</Badge>}
            <span className="text-xs text-slate-400">
              Applied {new Date(application.appliedAt).toLocaleDateString()}
            </span>
          </div>
        </div>

        <div className="flex shrink-0 items-center gap-2">
          <Button variant="secondary" onClick={() => setPreviewing(true)}>
            View resume
          </Button>
          <select
            className={selectClass}
            value={application.status}
            disabled={updating}
            onChange={(e) => onStatusChange(application.id, e.target.value)}
          >
            {APPLICATION_STATUSES.map((s) => (
              <option key={s.value} value={s.value}>
                {s.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      {previewing && (
        <ResumeFilePreviewModal
          resumeId={application.resumeId}
          fileType={application.resumeFileType}
          fileName={application.resumeFileName}
          onClose={() => setPreviewing(false)}
        />
      )}
    </Card>
  );
}
