import api from './api';

export function analyzeResume(resumeId, targetRole) {
  return api.post('/ats/analyze', { resumeId, targetRole: targetRole || null }).then((res) => res.data);
}

export function getAtsReport(resumeId) {
  return api.get(`/ats/report/${resumeId}`).then((res) => res.data);
}

/** params may include: page, size, sort ('newest' | 'oldest' | 'highestScore'). */
export function getAtsHistory(params = {}) {
  return api.get('/ats/history', { params: { page: 0, size: 10, ...params } }).then((res) => res.data);
}

export const ATS_HISTORY_SORT_OPTIONS = [
  { value: 'newest', label: 'Newest first' },
  { value: 'oldest', label: 'Oldest first' },
  { value: 'highestScore', label: 'Highest score' },
];

export function deleteAtsHistoryEntry(id) {
  return api.delete(`/ats/history/${id}`);
}

export const TARGET_ROLES = [
  { value: '', label: 'General (no specific role)' },
  { value: 'JAVA_DEVELOPER', label: 'Java Developer' },
  { value: 'PYTHON_DEVELOPER', label: 'Python Developer' },
  { value: 'FRONTEND_DEVELOPER', label: 'Frontend Developer' },
  { value: 'BACKEND_DEVELOPER', label: 'Backend Developer' },
  { value: 'FULL_STACK_DEVELOPER', label: 'Full Stack Developer' },
  { value: 'AI_ENGINEER', label: 'AI Engineer' },
  { value: 'DATA_ANALYST', label: 'Data Analyst' },
];

export function formatTargetRole(role) {
  const match = TARGET_ROLES.find((r) => r.value === role);
  return match ? match.label : 'General';
}
