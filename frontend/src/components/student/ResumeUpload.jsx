import { useRef, useState } from 'react';
import Card from '../ui/Card';
import Button from '../ui/Button';
import Alert from '../ui/Alert';
import { uploadResume } from '../../services/resumeService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const MAX_SIZE_BYTES = 5 * 1024 * 1024;
const ACCEPTED_EXTENSIONS = ['.pdf', '.docx'];

function hasAcceptedExtension(fileName) {
  const lower = fileName.toLowerCase();
  return ACCEPTED_EXTENSIONS.some((ext) => lower.endsWith(ext));
}

export default function ResumeUpload({ hasExistingResume, onUploaded }) {
  const inputRef = useRef(null);
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState('idle'); // idle | uploading | uploaded
  const [error, setError] = useState(null);

  function handleFileChange(event) {
    const selected = event.target.files?.[0] ?? null;
    setError(null);
    setStatus('idle');

    if (!selected) {
      setFile(null);
      return;
    }

    if (!hasAcceptedExtension(selected.name)) {
      setFile(null);
      setError('Only PDF and DOCX files are supported.');
      return;
    }

    if (selected.size > MAX_SIZE_BYTES) {
      setFile(null);
      setError('File is too large. Maximum size is 5MB.');
      return;
    }

    setFile(selected);
  }

  async function handleUpload() {
    if (!file) return;
    setStatus('uploading');
    setError(null);

    try {
      const response = await uploadResume(file);
      setStatus('uploaded');
      setFile(null);
      if (inputRef.current) inputRef.current.value = '';
      onUploaded?.(response);
    } catch (err) {
      setStatus('idle');
      setError(getErrorMessage(err, 'Failed to upload resume. Please try again.'));
    }
  }

  return (
    <Card title={hasExistingResume ? 'Replace resume' : 'Resume'}>
      <div className="space-y-3">
        {error && <Alert variant="error">{error}</Alert>}
        {status === 'uploaded' && !error && (
          <Alert variant="success">Resume uploaded and scored successfully.</Alert>
        )}

        <div className="flex items-center justify-between rounded-md border border-dashed border-slate-300 px-4 py-3">
          <div className="min-w-0">
            <p className="truncate text-sm font-medium text-slate-700">
              {file ? file.name : 'No file selected'}
            </p>
            <p className="text-xs text-slate-400">PDF or DOCX, up to 5MB</p>
          </div>
          <Button variant="secondary" type="button" onClick={() => inputRef.current?.click()}>
            Browse
          </Button>
          <input
            ref={inputRef}
            type="file"
            accept=".pdf,.docx"
            className="hidden"
            onChange={handleFileChange}
          />
        </div>

        <Button type="button" onClick={handleUpload} disabled={!file} loading={status === 'uploading'}>
          {hasExistingResume ? 'Replace resume' : 'Upload resume'}
        </Button>
      </div>
    </Card>
  );
}
