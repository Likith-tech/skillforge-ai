const LABELS = [
  { key: 'formatting', label: 'Formatting' },
  { key: 'skills', label: 'Skills' },
  { key: 'education', label: 'Education' },
  { key: 'experience', label: 'Experience' },
  { key: 'projects', label: 'Projects' },
  { key: 'certifications', label: 'Certifications' },
];

function barTone(value) {
  if (value >= 80) return 'bg-emerald-500';
  if (value >= 60) return 'bg-indigo-500';
  if (value >= 40) return 'bg-amber-500';
  return 'bg-rose-500';
}

export default function SubScoreBars({ subScores }) {
  return (
    <div className="space-y-3">
      {LABELS.map(({ key, label }) => {
        const value = subScores?.[key] ?? 0;
        return (
          <div key={key}>
            <div className="mb-1 flex items-center justify-between text-sm">
              <span className="font-medium text-slate-700">{label}</span>
              <span className="text-slate-500">{value}/100</span>
            </div>
            <div className="h-2 w-full overflow-hidden rounded-full bg-slate-100">
              <div
                className={`h-full rounded-full ${barTone(value)}`}
                style={{ width: `${value}%` }}
              />
            </div>
          </div>
        );
      })}
    </div>
  );
}
