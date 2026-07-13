import { useEffect, useState } from 'react';
import Button from '../ui/Button';
import Spinner from '../ui/Spinner';
import Alert from '../ui/Alert';
import { fetchResumeFileBlob } from '../../services/resumeService';
import { getErrorMessage } from '../../utils/getErrorMessage';

export default function ResumeFilePreviewModal({ resumeId, fileType, fileName, onClose }) {
  const [objectUrl, setObjectUrl] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    let url = null;
    let cancelled = false;

    fetchResumeFileBlob(resumeId)
      .then((blob) => {
        if (cancelled) return;
        url = URL.createObjectURL(blob);
        setObjectUrl(url);
      })
      .catch((err) => {
        if (!cancelled) setError(getErrorMessage(err, 'Failed to load resume file.'));
      });

    return () => {
      cancelled = true;
      if (url) URL.revokeObjectURL(url);
    };
  }, [resumeId]);

  const isPdf = fileType?.toUpperCase() === 'PDF';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 p-4">
      <div className="flex max-h-[85vh] w-full max-w-3xl flex-col rounded-xl bg-white shadow-xl">
        <div className="flex items-center justify-between border-b border-slate-200 px-5 py-3">
          <p className="truncate text-sm font-semibold text-slate-900">{fileName}</p>
          <div className="flex items-center gap-2">
            {objectUrl && (
              <a href={objectUrl} download={fileName}>
                <Button variant="secondary">Download</Button>
              </a>
            )}
            <Button variant="secondary" onClick={onClose}>
              Close
            </Button>
          </div>
        </div>

        <div className="flex-1 overflow-auto p-4">
          {error && <Alert variant="error">{error}</Alert>}
          {!error && !objectUrl && (
            <div className="flex justify-center py-16">
              <Spinner />
            </div>
          )}
          {!error && objectUrl && isPdf && (
            <iframe title={fileName} src={objectUrl} className="h-[65vh] w-full rounded-md border border-slate-200" />
          )}
          {!error && objectUrl && !isPdf && (
            <p className="py-16 text-center text-sm text-slate-500">
              DOCX files can&apos;t be previewed inline. Use the Download button above to open it.
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
