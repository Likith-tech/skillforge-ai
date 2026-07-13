import api from './api';

export function applyToJob(jobId) {
  return api.post('/applications', { jobId }).then((res) => res.data);
}

/** params may include: page, size, sort ('newest' | 'oldest' | 'highestScore' | 'jobTitle'). */
export function listMyApplications(params = {}) {
  return api.get('/applications/student', { params: { page: 0, size: 10, ...params } }).then((res) => res.data);
}

/** params may include: status, page, size, sort. */
export function listApplicationsForJob(jobId, params = {}) {
  return api
    .get(`/applications/job/${jobId}`, { params: { page: 0, size: 10, ...params } })
    .then((res) => res.data);
}

export const APPLICATION_SORT_OPTIONS = [
  { value: 'newest', label: 'Newest first' },
  { value: 'oldest', label: 'Oldest first' },
  { value: 'highestScore', label: 'Highest ATS score' },
  { value: 'jobTitle', label: 'Job title' },
];

export function updateApplicationStatus(id, status) {
  return api.patch(`/applications/${id}/status`, { status }).then((res) => res.data);
}

export const APPLICATION_STATUSES = [
  { value: 'APPLIED', label: 'Applied', tone: 'slate' },
  { value: 'UNDER_REVIEW', label: 'Reviewing', tone: 'amber' },
  { value: 'SHORTLISTED', label: 'Shortlisted', tone: 'indigo' },
  { value: 'INTERVIEWING', label: 'Interview', tone: 'indigo' },
  { value: 'HIRED', label: 'Selected', tone: 'emerald' },
  { value: 'REJECTED', label: 'Rejected', tone: 'rose' },
];

export function formatApplicationStatus(status) {
  return APPLICATION_STATUSES.find((s) => s.value === status)?.label ?? status;
}

export function applicationStatusTone(status) {
  return APPLICATION_STATUSES.find((s) => s.value === status)?.tone ?? 'slate';
}
