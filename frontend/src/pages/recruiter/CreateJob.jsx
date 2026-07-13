import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Input from '../../components/ui/Input';
import Button from '../../components/ui/Button';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import { createJob, updateJob, getJob, JOB_TYPES, EXPERIENCE_LEVELS } from '../../services/jobService';
import { getMyCompany } from '../../services/companyService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const EMPTY_FORM = {
  title: '',
  description: '',
  company: '',
  location: '',
  type: 'FULL_TIME',
  experienceLevel: '',
  salaryRange: '',
  requiredSkills: '',
};

const selectClass =
  'block w-full rounded-md border-0 px-3 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm';

export default function CreateJob() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();

  const [form, setForm] = useState(EMPTY_FORM);
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isEdit) {
      getJob(id)
        .then((job) =>
          setForm({
            title: job.title,
            description: job.description,
            company: job.company,
            location: job.location ?? '',
            type: job.type,
            experienceLevel: job.experienceLevel ?? '',
            salaryRange: job.salaryRange ?? '',
            requiredSkills: job.requiredSkills.join(', '),
          })
        )
        .catch((err) => setError(getErrorMessage(err, 'Failed to load this job.')))
        .finally(() => setLoading(false));
    } else {
      getMyCompany()
        .then((data) => setForm((prev) => ({ ...prev, company: data.companyName })))
        .catch(() => {});
    }
  }, [id, isEdit]);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSaving(true);
    setError(null);

    const payload = {
      ...form,
      experienceLevel: form.experienceLevel || null,
      requiredSkills: form.requiredSkills
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean),
    };

    try {
      if (isEdit) {
        await updateJob(id, payload);
      } else {
        await createJob(payload);
      }
      navigate('/recruiter/jobs');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to save this job.'));
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center py-16">
        <Spinner />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">{isEdit ? 'Edit job' : 'Post a job'}</h2>
        <p className="text-sm text-slate-500">
          {isEdit ? 'Update this posting.' : 'Create a new opening for students to discover.'}
        </p>
      </div>

      {error && <Alert variant="error">{error}</Alert>}

      <Card>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Input
            id="title"
            name="title"
            label="Job title"
            required
            value={form.title}
            onChange={handleChange}
            className="sm:col-span-2"
          />
          <Input id="company" name="company" label="Company" required value={form.company} onChange={handleChange} />
          <Input id="location" name="location" label="Location" value={form.location} onChange={handleChange} />

          <div>
            <label htmlFor="type" className="mb-1 block text-sm font-medium text-slate-700">
              Type
            </label>
            <select id="type" name="type" value={form.type} onChange={handleChange} className={selectClass}>
              {JOB_TYPES.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="experienceLevel" className="mb-1 block text-sm font-medium text-slate-700">
              Experience level
            </label>
            <select
              id="experienceLevel"
              name="experienceLevel"
              value={form.experienceLevel}
              onChange={handleChange}
              className={selectClass}
            >
              <option value="">Not specified</option>
              {EXPERIENCE_LEVELS.map((l) => (
                <option key={l.value} value={l.value}>
                  {l.label}
                </option>
              ))}
            </select>
          </div>

          <Input
            id="salaryRange"
            name="salaryRange"
            label="Salary range"
            placeholder="e.g. 8-12 LPA"
            value={form.salaryRange}
            onChange={handleChange}
          />
          <Input
            id="requiredSkills"
            name="requiredSkills"
            label="Required skills (comma-separated)"
            required
            placeholder="Java, Spring Boot, SQL"
            value={form.requiredSkills}
            onChange={handleChange}
          />

          <div className="sm:col-span-2">
            <label htmlFor="description" className="mb-1 block text-sm font-medium text-slate-700">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              required
              rows={6}
              value={form.description}
              onChange={handleChange}
              className={selectClass}
            />
          </div>

          <div className="sm:col-span-2">
            <Button type="submit" loading={saving}>
              {isEdit ? 'Save changes' : 'Post job'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}
