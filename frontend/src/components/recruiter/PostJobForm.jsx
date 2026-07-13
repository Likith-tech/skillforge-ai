import { useState } from 'react';
import Card from '../ui/Card';
import Input from '../ui/Input';
import Button from '../ui/Button';
import Badge from '../ui/Badge';

const EMPTY_FORM = { title: '', location: '', type: 'Full-time' };

/**
 * Frontend-only for now: no POST /jobs endpoint exists yet on the backend.
 * Posted jobs are held in local state so recruiters can see the flow end
 * to end before the API lands.
 */
export default function PostJobForm() {
  const [form, setForm] = useState(EMPTY_FORM);
  const [postedJobs, setPostedJobs] = useState([]);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!form.title.trim()) return;
    setPostedJobs((prev) => [{ ...form, id: Date.now() }, ...prev]);
    setForm(EMPTY_FORM);
  }

  return (
    <Card title="Post a job">
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
        <Input id="location" name="location" label="Location" value={form.location} onChange={handleChange} />
        <div>
          <label htmlFor="type" className="mb-1 block text-sm font-medium text-slate-700">
            Type
          </label>
          <select
            id="type"
            name="type"
            value={form.type}
            onChange={handleChange}
            className="block w-full rounded-md border-0 px-3 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm"
          >
            <option>Full-time</option>
            <option>Internship</option>
            <option>Contract</option>
          </select>
        </div>
        <div className="sm:col-span-2">
          <Button type="submit">Post job</Button>
        </div>
      </form>

      {postedJobs.length > 0 && (
        <ul className="mt-6 space-y-2 border-t border-slate-100 pt-4">
          {postedJobs.map((job) => (
            <li key={job.id} className="flex items-center justify-between text-sm">
              <span className="font-medium text-slate-900">
                {job.title} {job.location && `· ${job.location}`}
              </span>
              <Badge tone="emerald">{job.type}</Badge>
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
}
