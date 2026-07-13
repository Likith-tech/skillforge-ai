const CATEGORY_LABELS = {
  PROGRAMMING_LANGUAGE: 'Programming Languages',
  FRAMEWORK: 'Frameworks',
  DATABASE: 'Databases',
  CLOUD: 'Cloud',
  DEVOPS: 'DevOps',
  SOFT_SKILL: 'Soft Skills',
  OTHER: 'Other',
};

export default function SkillBreakdownChart({ breakdown }) {
  const entries = Object.entries(breakdown ?? {}).sort((a, b) => b[1] - a[1]);
  const max = Math.max(1, ...entries.map(([, count]) => count));

  if (entries.length === 0) {
    return <p className="text-sm text-slate-500">No skills detected yet.</p>;
  }

  return (
    <div className="space-y-2.5">
      {entries.map(([category, count]) => (
        <div key={category} className="flex items-center gap-3">
          <span className="w-40 shrink-0 text-xs font-medium text-slate-600">
            {CATEGORY_LABELS[category] ?? category}
          </span>
          <div className="h-2.5 flex-1 overflow-hidden rounded-full bg-slate-100">
            <div
              className="h-full rounded-full bg-indigo-500"
              style={{ width: `${(count / max) * 100}%` }}
            />
          </div>
          <span className="w-6 shrink-0 text-right text-xs text-slate-500">{count}</span>
        </div>
      ))}
    </div>
  );
}
