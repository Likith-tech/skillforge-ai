import Card from '../ui/Card';
import Badge from '../ui/Badge';

// Dummy data until GET /recruiter/candidates exists.
const CANDIDATES = [
  { id: 1, name: 'Priya Sharma', role: 'Frontend Engineer', score: 88, status: 'Shortlisted' },
  { id: 2, name: 'Arjun Mehta', role: 'Backend Engineer', score: 74, status: 'New' },
  { id: 3, name: 'Sara Iqbal', role: 'Data Analyst', score: 91, status: 'Interviewing' },
];

const STATUS_TONE = {
  New: 'slate',
  Shortlisted: 'indigo',
  Interviewing: 'amber',
  Rejected: 'rose',
};

export default function CandidateList() {
  return (
    <Card title="Candidates">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-100 text-sm">
          <thead>
            <tr className="text-left text-xs font-semibold uppercase tracking-wide text-slate-400">
              <th className="py-2 pr-4">Name</th>
              <th className="py-2 pr-4">Applying for</th>
              <th className="py-2 pr-4">ATS score</th>
              <th className="py-2 pr-4">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {CANDIDATES.map((candidate) => (
              <tr key={candidate.id}>
                <td className="py-3 pr-4 font-medium text-slate-900">{candidate.name}</td>
                <td className="py-3 pr-4 text-slate-600">{candidate.role}</td>
                <td className="py-3 pr-4 text-slate-600">{candidate.score}</td>
                <td className="py-3 pr-4">
                  <Badge tone={STATUS_TONE[candidate.status]}>{candidate.status}</Badge>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  );
}
