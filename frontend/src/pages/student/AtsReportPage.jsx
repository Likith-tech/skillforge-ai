import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Badge from '../../components/ui/Badge';
import Button from '../../components/ui/Button';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import CircularScore from '../../components/ui/CircularScore';
import Pagination from '../../components/ui/Pagination';
import SortSelect from '../../components/ui/SortSelect';
import SubScoreBars from '../../components/student/SubScoreBars';
import SkillBreakdownChart from '../../components/student/SkillBreakdownChart';
import MissingSkillsPanel from '../../components/student/MissingSkillsPanel';
import AtsHistoryList from '../../components/student/AtsHistoryList';
import { useCurrentResume } from '../../hooks/useCurrentResume';
import {
  analyzeResume,
  getAtsReport,
  getAtsHistory,
  TARGET_ROLES,
  formatTargetRole,
  ATS_HISTORY_SORT_OPTIONS,
} from '../../services/atsService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const PAGE_SIZE = 10;

const STRENGTH_TONE = {
  Strong: 'emerald',
  Good: 'indigo',
  'Needs Improvement': 'amber',
  Weak: 'rose',
};

export default function AtsReportPage() {
  const { resume, loading: resumeLoading } = useCurrentResume();

  const [report, setReport] = useState(null);
  const [reportLoading, setReportLoading] = useState(true);
  const [reportError, setReportError] = useState(null);

  const [targetRole, setTargetRole] = useState('');
  const [analyzing, setAnalyzing] = useState(false);

  const [historySort, setHistorySort] = useState('newest');
  const [historyPageNum, setHistoryPageNum] = useState(0);
  const [historyPage, setHistoryPage] = useState({ content: [], page: 0, totalPages: 0, totalElements: 0 });
  const [historyLoading, setHistoryLoading] = useState(true);

  const refreshHistory = useCallback(() => {
    setHistoryLoading(true);
    getAtsHistory({ sort: historySort, page: historyPageNum, size: PAGE_SIZE })
      .then((data) => setHistoryPage(data))
      .catch(() => setHistoryPage({ content: [], page: 0, totalPages: 0, totalElements: 0 }))
      .finally(() => setHistoryLoading(false));
  }, [historySort, historyPageNum]);

  const loadReport = useCallback((resumeId) => {
    setReportLoading(true);
    setReportError(null);
    getAtsReport(resumeId)
      .then((data) => setReport(data))
      .catch((err) => setReportError(getErrorMessage(err, 'Failed to load ATS report.')))
      .finally(() => setReportLoading(false));
  }, []);

  useEffect(() => {
    if (resume?.id) {
      loadReport(resume.id);
    } else if (!resumeLoading) {
      setReportLoading(false);
    }
  }, [resume?.id, resumeLoading, loadReport]);

  useEffect(() => {
    refreshHistory();
  }, [refreshHistory]);

  useEffect(() => {
    setHistoryPageNum(0);
  }, [historySort]);

  async function handleAnalyze() {
    if (!resume?.id) return;
    setAnalyzing(true);
    setReportError(null);
    try {
      const data = await analyzeResume(resume.id, targetRole || null);
      setReport(data);
      refreshHistory();
    } catch (err) {
      setReportError(getErrorMessage(err, 'Failed to analyze resume.'));
    } finally {
      setAnalyzing(false);
    }
  }

  if (!resumeLoading && !resume) {
    return (
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">ATS Report</h2>
          <p className="text-sm text-slate-500">Deep resume analysis, skill breakdown, and role comparison.</p>
        </div>
        <Card>
          <p className="text-sm text-slate-600">
            You haven&apos;t uploaded a resume yet.{' '}
            <Link to="/student/resume" className="font-semibold text-indigo-600 hover:text-indigo-500">
              Upload one
            </Link>{' '}
            to generate your ATS report.
          </p>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">ATS Report</h2>
        <p className="text-sm text-slate-500">Deep resume analysis, skill breakdown, and role comparison.</p>
      </div>

      {reportError && <Alert variant="error">{reportError}</Alert>}

      <Card>
        <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <label htmlFor="targetRole" className="mb-1 block text-sm font-medium text-slate-700">
              Compare against a target role
            </label>
            <select
              id="targetRole"
              value={targetRole}
              onChange={(e) => setTargetRole(e.target.value)}
              className="block w-full min-w-[16rem] rounded-md border-0 px-3 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm"
            >
              {TARGET_ROLES.map((role) => (
                <option key={role.value} value={role.value}>
                  {role.label}
                </option>
              ))}
            </select>
          </div>
          <Button onClick={handleAnalyze} loading={analyzing} disabled={resumeLoading || !resume}>
            Run analysis
          </Button>
        </div>
      </Card>

      {reportLoading || resumeLoading ? (
        <Card>
          <div className="flex justify-center py-10">
            <Spinner />
          </div>
        </Card>
      ) : report ? (
        <>
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
            <Card title="Overall strength" className="lg:col-span-1">
              <div className="flex flex-col items-center gap-3 py-2">
                <CircularScore score={report.overallScore} size="lg" />
                <Badge tone={STRENGTH_TONE[report.strengthLabel] ?? 'slate'}>{report.strengthLabel}</Badge>
                <p className="text-center text-xs text-slate-500">
                  Analyzed against {formatTargetRole(report.targetRole)}
                  <br />
                  {new Date(report.analyzedAt).toLocaleString()}
                </p>
              </div>
            </Card>

            <Card title="Score breakdown" className="lg:col-span-2">
              <SubScoreBars subScores={report.subScores} />
            </Card>
          </div>

          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            <Card title="Skill breakdown">
              <SkillBreakdownChart breakdown={report.skillBreakdown} />
            </Card>
            <Card title="Missing skills">
              <MissingSkillsPanel missingSkills={report.missingSkills} />
            </Card>
          </div>

          <Card title="Detected contact info">
            <dl className="grid grid-cols-1 gap-3 text-sm sm:grid-cols-3">
              <div>
                <dt className="text-xs font-semibold uppercase tracking-wide text-slate-400">Name</dt>
                <dd className="text-slate-700">{report.extractedName ?? 'Not detected'}</dd>
              </div>
              <div>
                <dt className="text-xs font-semibold uppercase tracking-wide text-slate-400">Email</dt>
                <dd className="text-slate-700">{report.extractedEmail ?? 'Not detected'}</dd>
              </div>
              <div>
                <dt className="text-xs font-semibold uppercase tracking-wide text-slate-400">Phone</dt>
                <dd className="text-slate-700">{report.extractedPhone ?? 'Not detected'}</dd>
              </div>
            </dl>
          </Card>

          <Card title="Improvement tips">
            <ul className="list-inside list-disc space-y-1.5 text-sm text-slate-600">
              {report.suggestions.map((suggestion) => (
                <li key={suggestion}>{suggestion}</li>
              ))}
            </ul>
          </Card>
        </>
      ) : null}

      <div className="flex justify-end">
        <SortSelect id="ats-history-sort" value={historySort} onChange={setHistorySort} options={ATS_HISTORY_SORT_OPTIONS} />
      </div>
      <AtsHistoryList history={historyPage.content} loading={historyLoading} onChanged={refreshHistory} />
      <Pagination
        page={historyPage.page}
        totalPages={historyPage.totalPages}
        totalElements={historyPage.totalElements}
        onPageChange={setHistoryPageNum}
      />
    </div>
  );
}
