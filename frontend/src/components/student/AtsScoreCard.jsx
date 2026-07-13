import { Link } from 'react-router-dom';
import Card from '../ui/Card';
import Spinner from '../ui/Spinner';
import CircularScore from '../ui/CircularScore';

function scoreCopy(score) {
  if (score >= 80) return 'Your resume is strongly optimized for ATS screening.';
  if (score >= 60) return 'Your resume is more optimized than average, with room to improve.';
  if (score >= 40) return 'Your resume needs some work to pass ATS screening reliably.';
  return 'Your resume is likely to be filtered out by ATS screening.';
}

export default function AtsScoreCard({ resume, loading }) {
  if (loading) {
    return (
      <Card title="ATS Score">
        <div className="flex justify-center py-6">
          <Spinner />
        </div>
      </Card>
    );
  }

  if (!resume) {
    return (
      <Card title="ATS Score">
        <p className="text-sm text-slate-500">Upload a resume to see your ATS score.</p>
      </Card>
    );
  }

  const score = resume.atsScore ?? 0;

  return (
    <Card title="ATS Score">
      <div className="flex items-center gap-4">
        <CircularScore score={score} />
        <div>
          <p className="text-sm text-slate-600">{scoreCopy(score)}</p>
          <p className="mt-1 text-xs text-slate-400">
            Based on {resume.originalFileName}, uploaded {new Date(resume.uploadedAt).toLocaleDateString()}.
          </p>
        </div>
      </div>
      <Link
        to="/student/ats-report"
        className="mt-4 inline-block text-sm font-semibold text-indigo-600 hover:text-indigo-500"
      >
        View full ATS report &rarr;
      </Link>
    </Card>
  );
}
