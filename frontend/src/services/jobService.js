import api from './api';

/** filters may include: keyword, location, type, experienceLevel, skill, company, page, size, sort.
 *  Resolves to a PageResponse: { content, page, size, totalElements, totalPages, last }. */
export function listJobs(filters = {}) {
  const params = {};
  if (filters.keyword) params.keyword = filters.keyword;
  if (filters.location) params.location = filters.location;
  if (filters.type) params.type = filters.type;
  if (filters.experienceLevel) params.experienceLevel = filters.experienceLevel;
  if (filters.skill) params.skill = filters.skill;
  if (filters.company) params.company = filters.company;
  if (filters.sort) params.sort = filters.sort;
  params.page = filters.page ?? 0;
  params.size = filters.size ?? 10;
  return api.get('/jobs', { params }).then((res) => res.data);
}

export const JOB_SORT_OPTIONS = [
  { value: 'newest', label: 'Newest first' },
  { value: 'oldest', label: 'Oldest first' },
  { value: 'companyName', label: 'Company name' },
  { value: 'jobTitle', label: 'Job title' },
  { value: 'salary', label: 'Salary' },
];

export function getJob(id) {
  return api.get(`/jobs/${id}`).then((res) => res.data);
}

export function createJob(payload) {
  return api.post('/jobs', payload).then((res) => res.data);
}

export function updateJob(id, payload) {
  return api.put(`/jobs/${id}`, payload).then((res) => res.data);
}

export function deleteJob(id) {
  return api.delete(`/jobs/${id}`);
}

export function setJobStatus(id, status) {
  return api.patch(`/jobs/${id}/status`, { status }).then((res) => res.data);
}

export function listMyJobs(params = {}) {
  return api.get('/jobs/mine', { params: { page: 0, size: 10, ...params } }).then((res) => res.data);
}

export function listAllJobsAdmin(params = {}) {
  return api.get('/jobs/admin', { params: { page: 0, size: 10, ...params } }).then((res) => res.data);
}

export function getRecommendedJobs() {
  return api.get('/jobs/recommended').then((res) => res.data);
}

export function saveJob(id) {
  return api.post(`/jobs/${id}/save`);
}

export function unsaveJob(id) {
  return api.delete(`/jobs/${id}/save`);
}

export function listSavedJobs() {
  return api.get('/jobs/saved').then((res) => res.data);
}

export const JOB_TYPES = [
  { value: 'FULL_TIME', label: 'Full-time' },
  { value: 'INTERNSHIP', label: 'Internship' },
  { value: 'CONTRACT', label: 'Contract' },
];

export const EXPERIENCE_LEVELS = [
  { value: 'ENTRY', label: 'Entry' },
  { value: 'MID', label: 'Mid' },
  { value: 'SENIOR', label: 'Senior' },
  { value: 'LEAD', label: 'Lead' },
];

export function formatJobType(type) {
  return JOB_TYPES.find((t) => t.value === type)?.label ?? type;
}

export function formatExperienceLevel(level) {
  if (!level) return null;
  return EXPERIENCE_LEVELS.find((l) => l.value === level)?.label ?? level;
}
