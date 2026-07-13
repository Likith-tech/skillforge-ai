import Input from '../ui/Input';
import { JOB_TYPES, EXPERIENCE_LEVELS } from '../../services/jobService';

const selectClass =
  'block w-full rounded-md border-0 px-3 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm';

export default function FilterPanel({ filters, onChange }) {
  function handle(field) {
    return (event) => onChange(field, event.target.value);
  }

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-5">
      <Input
        id="filter-location"
        label="Location"
        placeholder="e.g. Remote, Bengaluru"
        value={filters.location}
        onChange={handle('location')}
      />
      <Input
        id="filter-skill"
        label="Skill"
        placeholder="e.g. Java"
        value={filters.skill}
        onChange={handle('skill')}
      />
      <Input
        id="filter-company"
        label="Company"
        placeholder="e.g. Acme Corp"
        value={filters.company}
        onChange={handle('company')}
      />
      <div>
        <label htmlFor="filter-type" className="mb-1 block text-sm font-medium text-slate-700">
          Type
        </label>
        <select id="filter-type" value={filters.type} onChange={handle('type')} className={selectClass}>
          <option value="">Any type</option>
          {JOB_TYPES.map((t) => (
            <option key={t.value} value={t.value}>
              {t.label}
            </option>
          ))}
        </select>
      </div>
      <div>
        <label htmlFor="filter-experience" className="mb-1 block text-sm font-medium text-slate-700">
          Experience level
        </label>
        <select
          id="filter-experience"
          value={filters.experienceLevel}
          onChange={handle('experienceLevel')}
          className={selectClass}
        >
          <option value="">Any level</option>
          {EXPERIENCE_LEVELS.map((l) => (
            <option key={l.value} value={l.value}>
              {l.label}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}
