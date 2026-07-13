import { useCallback, useEffect, useState } from 'react';
import { useAuth } from './useAuth';
import { getCurrentResume } from '../services/resumeService';

/**
 * Shared fetch of the logged-in student's current (most recent) resume, used
 * by both the dashboard summary widgets and the full Resume management page
 * so they always agree on what "current" means without duplicating state.
 */
export function useCurrentResume() {
  const { user } = useAuth();
  const [resume, setResume] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const refresh = useCallback(() => {
    if (!user?.id) {
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    getCurrentResume(user.id)
      .then((data) => setResume(data))
      .catch((err) => {
        if (err?.response?.status === 404) {
          setResume(null);
        } else {
          setError(err);
        }
      })
      .finally(() => setLoading(false));
  }, [user?.id]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  return { resume, loading, error, refresh };
}
