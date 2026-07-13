import { useCallback, useEffect, useState } from 'react';
import Card from '../../components/ui/Card';
import Badge from '../../components/ui/Badge';
import Spinner from '../../components/ui/Spinner';
import Alert from '../../components/ui/Alert';
import Pagination from '../../components/ui/Pagination';
import SortSelect from '../../components/ui/SortSelect';
import ResumeUpload from '../../components/student/ResumeUpload';
import AtsScoreCard from '../../components/student/AtsScoreCard';
import ResumeHistoryList from '../../components/student/ResumeHistoryList';
import { useCurrentResume } from '../../hooks/useCurrentResume';
import { getResumeHistory, RESUME_SORT_OPTIONS } from '../../services/resumeService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const PAGE_SIZE = 10;

function ResumeDetailsCard({ resume, loading }) {
  if (loading) {
    return (
      <Card title="Current resume">
        <div className="flex justify-center py-8">
          <Spinner />
        </div>
      </Card>
    );
  }

  if (!resume) {
    return (
      <Card title="Current resume">
        <p className="text-sm text-slate-500">Upload a resume below to get started.</p>
      </Card>
    );
  }

  return (
    <Card title="Current resume">
      <div className="space-y-4">
        <div>
          <p className="text-sm font-medium text-slate-900">{resume.originalFileName}</p>
          <p className="text-xs text-slate-500">
            Uploaded {new Date(resume.uploadedAt).toLocaleDateString()} &middot; {resume.fileType}
          </p>
        </div>

        {resume.skills?.length > 0 && (
          <div>
            <p className="mb-1.5 text-xs font-semibold uppercase tracking-wide text-slate-400">
              Skills detected
            </p>
            <div className="flex flex-wrap gap-1.5">
              {resume.skills.map((skill) => (
                <Badge key={skill} tone="indigo">
                  {skill}
                </Badge>
              ))}
            </div>
          </div>
        )}

        {resume.missingCoreSkills?.length > 0 && (
          <div>
            <p className="mb-1.5 text-xs font-semibold uppercase tracking-wide text-slate-400">
              Missing core skills
            </p>
            <div className="flex flex-wrap gap-1.5">
              {resume.missingCoreSkills.map((skill) => (
                <Badge key={skill} tone="amber">
                  {skill}
                </Badge>
              ))}
            </div>
          </div>
        )}

        {resume.suggestions?.length > 0 && (
          <div>
            <p className="mb-1.5 text-xs font-semibold uppercase tracking-wide text-slate-400">
              Suggestions
            </p>
            <ul className="list-inside list-disc space-y-1 text-sm text-slate-600">
              {resume.suggestions.map((suggestion) => (
                <li key={suggestion}>{suggestion}</li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </Card>
  );
}

export default function ResumePage() {
  const { resume, loading: resumeLoading, error: resumeError, refresh: refreshCurrent } = useCurrentResume();
  const [sort, setSort] = useState('newest');
  const [page, setPage] = useState(0);
  const [historyPage, setHistoryPage] = useState({ content: [], page: 0, totalPages: 0, totalElements: 0 });
  const [historyLoading, setHistoryLoading] = useState(true);
  const [historyError, setHistoryError] = useState(null);

  const refreshHistory = useCallback(() => {
    setHistoryLoading(true);
    setHistoryError(null);
    getResumeHistory({ sort, page, size: PAGE_SIZE })
      .then((data) => setHistoryPage(data))
      .catch((err) => setHistoryError(getErrorMessage(err, 'Failed to load resume history.')))
      .finally(() => setHistoryLoading(false));
  }, [sort, page]);

  useEffect(() => {
    refreshHistory();
  }, [refreshHistory]);

  useEffect(() => {
    setPage(0);
  }, [sort]);

  function handleChanged() {
    refreshCurrent();
    refreshHistory();
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Resume</h2>
        <p className="text-sm text-slate-500">Upload, replace, and manage every resume you've submitted.</p>
      </div>

      {resumeError && <Alert variant="error">{getErrorMessage(resumeError, 'Failed to load your resume.')}</Alert>}

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div className="space-y-6 lg:col-span-1">
          <AtsScoreCard resume={resume} loading={resumeLoading} />
          <ResumeUpload hasExistingResume={Boolean(resume)} onUploaded={handleChanged} />
        </div>
        <div className="space-y-6 lg:col-span-2">
          <ResumeDetailsCard resume={resume} loading={resumeLoading} />
          <div className="flex justify-end">
            <SortSelect id="resume-history-sort" value={sort} onChange={setSort} options={RESUME_SORT_OPTIONS} />
          </div>
          <ResumeHistoryList resumes={historyPage.content} loading={historyLoading} onChanged={handleChanged} />
          <Pagination
            page={historyPage.page}
            totalPages={historyPage.totalPages}
            totalElements={historyPage.totalElements}
            onPageChange={setPage}
          />
          {historyError && <Alert variant="error">{historyError}</Alert>}
        </div>
      </div>
    </div>
  );
}
