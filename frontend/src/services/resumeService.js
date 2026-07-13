import api from './api';

export function uploadResume(file) {
  const formData = new FormData();
  formData.append('file', file);

  return api
    .post('/resume/upload', formData, {
      // The shared `api` instance defaults Content-Type to application/json for
      // every request. Setting it to null here removes that default so the
      // browser can attach its own multipart boundary - if axios sends an
      // explicit "multipart/form-data" header without one, Spring can't parse it.
      headers: { 'Content-Type': null },
    })
    .then((res) => res.data);
}

export function getCurrentResume(userId) {
  return api.get(`/resume/${userId}`).then((res) => res.data);
}

/** params may include: page, size, sort ('newest' | 'oldest' | 'highestScore'). */
export function getResumeHistory(params = {}) {
  return api.get('/resumes', { params: { page: 0, size: 10, ...params } }).then((res) => res.data);
}

export const RESUME_SORT_OPTIONS = [
  { value: 'newest', label: 'Newest first' },
  { value: 'oldest', label: 'Oldest first' },
  { value: 'highestScore', label: 'Highest ATS score' },
];

export function getResumeById(resumeId) {
  return api.get(`/resumes/${resumeId}`).then((res) => res.data);
}

export function deleteResume(resumeId) {
  return api.delete(`/resumes/${resumeId}`);
}

/** Fetches the resume's raw file as a Blob so it can be previewed/downloaded via an object URL. */
export function fetchResumeFileBlob(resumeId) {
  return api.get(`/resumes/${resumeId}/file`, { responseType: 'blob' }).then((res) => res.data);
}
