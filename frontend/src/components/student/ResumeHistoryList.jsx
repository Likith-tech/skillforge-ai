import { useState } from 'react';
import Card from '../ui/Card';
import Badge from '../ui/Badge';
import Button from '../ui/Button';
import Alert from '../ui/Alert';
import Spinner from '../ui/Spinner';
import ResumeFilePreviewModal from './ResumeFilePreviewModal';
import { deleteResume } from '../../services/resumeService';
import { getErrorMessage } from '../../utils/getErrorMessage';

function formatSize(bytes) {
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
}

export default function ResumeHistoryList({ resumes, loading, onChanged }) {
  const [previewing, setPreviewing] = useState(null);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState(null);

  async function handleDelete(resume) {
    if (!window.confirm(`Delete "${resume.originalFileName}"? This can't be undone.`)) {
      return;
    }

    setError(null);
    setDeletingId(resume.id);
    try {
      await deleteResume(resume.id);
      onChanged?.();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to delete resume.'));
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <Card title="Resume history">
      {error && <Alert variant="error">{error}</Alert>}

      {loading ? (
        <div className="flex justify-center py-8">
          <Spinner />
        </div>
      ) : resumes.length === 0 ? (
        <p className="text-sm text-slate-500">No resumes uploaded yet.</p>
      ) : (
        <ul className="divide-y divide-slate-100">
          {resumes.map((resume) => (
            <li key={resume.id} className="flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
              <div className="min-w-0">
                <div className="flex items-center gap-2">
                  <p className="truncate text-sm font-medium text-slate-900">{resume.originalFileName}</p>
                  {resume.current && <Badge tone="emerald">Current</Badge>}
                </div>
                <p className="text-xs text-slate-500">
                  {resume.fileType} &middot; {formatSize(resume.fileSizeBytes)} &middot; Uploaded{' '}
                  {new Date(resume.uploadedAt).toLocaleDateString()}
                  {resume.atsScore != null && <> &middot; ATS score {resume.atsScore}</>}
                </p>
              </div>
              <div className="flex shrink-0 items-center gap-2">
                <Button variant="secondary" onClick={() => setPreviewing(resume)}>
                  Preview
                </Button>
                <Button
                  variant="danger"
                  onClick={() => handleDelete(resume)}
                  loading={deletingId === resume.id}
                >
                  Delete
                </Button>
              </div>
            </li>
          ))}
        </ul>
      )}

      {previewing && (
        <ResumeFilePreviewModal
          resumeId={previewing.id}
          fileType={previewing.fileType}
          fileName={previewing.originalFileName}
          onClose={() => setPreviewing(null)}
        />
      )}
    </Card>
  );
}
