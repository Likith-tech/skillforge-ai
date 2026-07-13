import { useEffect, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Badge from '../../components/ui/Badge';
import Button from '../../components/ui/Button';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import { getJob, saveJob, unsaveJob, listSavedJobs, formatJobType, formatExperienceLevel } from '../../services/jobService';
import { applyToJob } from '../../services/applicationService';
import { getErrorMessage } from '../../utils/getErrorMessage';

export default function JobDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [saved, setSaved] = useState(false);
  const [applying, setApplying] = useState(false);
  const [applyError, setApplyError] = useState(null);
  const [applied, setApplied] = useState(false);

  useEffect(() => {
    setLoading(true);
    getJob(id)
      .then((data) => setJob(data))
      .catch((err) => setError(getErrorMessage(err, 'Failed to load this job.')))
      .finally(() => setLoading(false));

    listSavedJobs()
      .then((data) => setSaved(data.some((s) => s.job.id === Number(id))))
      .catch(() => setSaved(false));
  }, [id]);

  async function handleToggleSave() {
    try {
      if (saved) {
        await unsaveJob(id);
        setSaved(false);
      } else {
        await saveJob(id);
        setSaved(true);
      }
    } catch (err) {
      setApplyError(getErrorMessage(err, 'Failed to update saved jobs.'));
    }
  }

  async function handleApply() {
    setApplying(true);
    setApplyError(null);
    try {
      await applyToJob(Number(id));
      setApplied(true);
    } catch (err) {
      setApplyError(getErrorMessage(err, 'Failed to submit your application.'));
    } finally {
      setApplying(false);
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center py-16">
        <Spinner />
      </div>
    );
  }

  if (error || !job) {
    return <Alert variant="error">{error ?? 'Job not found.'}</Alert>;
  }

  return (
    <div className="space-y-6">
      <Card>
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-start">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">{job.title}</h2>
            <p className="mt-1 text-sm text-slate-500">
              {job.company}
              {job.location && <> &middot; {job.location}</>}
            </p>
            <div className="mt-3 flex flex-wrap gap-1.5">
              <Badge tone="slate">{formatJobType(job.type)}</Badge>
              {job.experienceLevel && <Badge tone="indigo">{formatExperienceLevel(job.experienceLevel)}</Badge>}
              {job.salaryRange && <Badge tone="emerald">{job.salaryRange}</Badge>}
              <Badge tone={job.status === 'OPEN' ? 'emerald' : 'rose'}>{job.status}</Badge>
            </div>
          </div>
          <div className="flex shrink-0 items-center gap-2">
            <Button variant="secondary" onClick={handleToggleSave}>
              {saved ? '★ Saved' : '☆ Save job'}
            </Button>
          </div>
        </div>
      </Card>

      {applyError && <Alert variant="error">{applyError}</Alert>}
      {applied && <Alert variant="success">Application submitted! Track it from My Applications.</Alert>}

      <Card title="Job description">
        <p className="whitespace-pre-line text-sm text-slate-700">{job.description}</p>
      </Card>

      <Card title="Required skills">
        <div className="flex flex-wrap gap-1.5">
          {job.requiredSkills.map((skill) => (
            <Badge key={skill} tone="indigo">
              {skill}
            </Badge>
          ))}
        </div>
      </Card>

      <Card>
        <div className="flex items-center justify-between">
          <p className="text-sm text-slate-500">
            {job.status !== 'OPEN'
              ? 'This job is no longer accepting applications.'
              : 'Applying uses your most recently uploaded resume.'}
          </p>
          {!applied && (
            <Button onClick={handleApply} loading={applying} disabled={job.status !== 'OPEN'}>
              Apply now
            </Button>
          )}
        </div>
        {applyError?.toLowerCase().includes('resume') && (
          <p className="mt-2 text-sm">
            <Link to="/student/resume" className="font-semibold text-indigo-600 hover:text-indigo-500">
              Upload a resume
            </Link>{' '}
            before applying.
          </p>
        )}
      </Card>

      <Button variant="secondary" onClick={() => navigate(-1)}>
        &larr; Back
      </Button>
    </div>
  );
}
