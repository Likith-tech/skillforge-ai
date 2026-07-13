import { useState } from 'react';
import Card from '../ui/Card';
import Badge from '../ui/Badge';
import Button from '../ui/Button';
import Alert from '../ui/Alert';
import Spinner from '../ui/Spinner';
import { deleteAtsHistoryEntry, formatTargetRole } from '../../services/atsService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const STRENGTH_TONE = {
  Strong: 'emerald',
  Good: 'indigo',
  'Needs Improvement': 'amber',
  Weak: 'rose',
};

export default function AtsHistoryList({ history, loading, onChanged }) {
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState(null);

  async function handleDelete(entry) {
    if (!window.confirm('Delete this ATS analysis from your history?')) return;

    setError(null);
    setDeletingId(entry.id);
    try {
      await deleteAtsHistoryEntry(entry.id);
      onChanged?.();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to delete this analysis.'));
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <Card title="Analysis history">
      {error && <Alert variant="error">{error}</Alert>}

      {loading ? (
        <div className="flex justify-center py-8">
          <Spinner />
        </div>
      ) : history.length === 0 ? (
        <p className="text-sm text-slate-500">No analyses yet.</p>
      ) : (
        <ul className="divide-y divide-slate-100">
          {history.map((entry) => (
            <li key={entry.id} className="flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
              <div className="min-w-0">
                <div className="flex items-center gap-2">
                  <p className="truncate text-sm font-medium text-slate-900">{entry.resumeFileName}</p>
                  <Badge tone={STRENGTH_TONE[entry.strengthLabel] ?? 'slate'}>{entry.strengthLabel}</Badge>
                </div>
                <p className="text-xs text-slate-500">
                  {formatTargetRole(entry.targetRole)} &middot; Score {entry.overallScore} &middot;{' '}
                  {new Date(entry.analyzedAt).toLocaleString()}
                </p>
              </div>
              <Button variant="danger" onClick={() => handleDelete(entry)} loading={deletingId === entry.id}>
                Delete
              </Button>
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
}
