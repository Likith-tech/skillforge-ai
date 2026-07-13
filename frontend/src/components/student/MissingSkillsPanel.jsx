import Badge from '../ui/Badge';

const PRIORITY_TONE = { HIGH: 'rose', MEDIUM: 'amber', LOW: 'slate' };
const PRIORITY_ORDER = ['HIGH', 'MEDIUM', 'LOW'];
const PRIORITY_LABEL = { HIGH: 'High priority', MEDIUM: 'Medium priority', LOW: 'Low priority' };

export default function MissingSkillsPanel({ missingSkills }) {
  if (!missingSkills || missingSkills.length === 0) {
    return <p className="text-sm text-slate-500">No missing skills detected - nice work.</p>;
  }

  const grouped = PRIORITY_ORDER.map((priority) => ({
    priority,
    skills: missingSkills.filter((skill) => skill.priority === priority),
  })).filter((group) => group.skills.length > 0);

  return (
    <div className="space-y-3">
      {grouped.map(({ priority, skills }) => (
        <div key={priority}>
          <p className="mb-1.5 text-xs font-semibold uppercase tracking-wide text-slate-400">
            {PRIORITY_LABEL[priority]}
          </p>
          <div className="flex flex-wrap gap-1.5">
            {skills.map((skill) => (
              <Badge key={skill.skillName} tone={PRIORITY_TONE[priority]}>
                {skill.skillName}
              </Badge>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
