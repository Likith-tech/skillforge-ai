import apiClient from "./apiClient";

export const getJobs = async (params = {}) => {
  const response = await apiClient.get("/jobs", { params });
  return response.data;
};

export const getJob = async (jobId) => {
  const response = await apiClient.get(`/jobs/${jobId}`);
  return response.data;
};

export const getMyJobs = async () => {
  const response = await apiClient.get("/jobs/me");
  return response.data;
};

export const getRecruiterApplicants = async (jobId) => (await apiClient.get(`/recruiter/workflow/jobs/${jobId}/applicants`)).data;
export const getRankedApplicants = async (jobId) => (await apiClient.get(`/recruiter/workflow/jobs/${jobId}/applicants/ranked`)).data;
export const shortlistApplicant = async (applicationId) => (await apiClient.post(`/recruiter/workflow/applications/${applicationId}/shortlist`)).data;
export const rejectApplicant = async (applicationId) => (await apiClient.post(`/recruiter/workflow/applications/${applicationId}/reject`)).data;
export const scheduleInterview = async (applicationId, interviewAt, meetingLink, notes) => (await apiClient.post(`/recruiter/workflow/applications/${applicationId}/interview`, null, { params: { interviewAt, meetingLink, notes } })).data;
export const sendOffer = async (applicationId, offerTitle, offerDescription) => (await apiClient.post(`/recruiter/workflow/applications/${applicationId}/offer`, null, { params: { offerTitle, offerDescription } })).data;
export const withdrawOffer = async (applicationId) => (await apiClient.delete(`/recruiter/workflow/applications/${applicationId}/offer`)).data;
export const getCandidateTimeline = async (jobId) => (await apiClient.get(`/recruiter/workflow/jobs/${jobId}/timeline`)).data;
export const getApplicantCount = async (jobId, status) => (await apiClient.get(`/recruiter/workflow/jobs/${jobId}/status-count`, { params: { status } })).data;

export const createJob = async (payload) => {
  const response = await apiClient.post("/jobs", payload);
  return response.data;
};

export const updateJob = async (jobId, payload) => {
  const response = await apiClient.put(`/jobs/${jobId}`, payload);
  return response.data;
};

export const updateJobStatus = async (jobId, status) => {
  const response = await apiClient.put(`/jobs/${jobId}/status`, null, { params: { status } });
  return response.data;
};

export const refreshRecommendations = async () => {
  const response = await apiClient.post("/students/me/recommendations/refresh");
  return response.data;
};

export const getRecommendations = async () => {
  const response = await apiClient.get("/students/me/recommendations");
  return response.data;
};

export const getSkillGaps = async () => {
  const response = await apiClient.get("/students/me/skill-gaps");
  return response.data;
};

export const getPlacementScore = async () => {
  const response = await apiClient.get("/students/me/placement-score");
  return response.data;
};

export const getRoadmaps = async () => {
  const response = await apiClient.get("/students/me/roadmaps");
  return response.data;
};

export const getStudentDashboard = async () => {
  const response = await apiClient.get("/students/me/dashboard");
  return response.data;
};

export const getJobInsights = async () => {
  const response = await apiClient.get("/students/me/insights");
  return response.data;
};

export const getAppliedJobs = async () => (await apiClient.get("/students/me/placement/applied-jobs")).data;
export const getSavedJobs = async () => (await apiClient.get("/students/me/placement/saved-jobs")).data;
export const getInterviewSchedules = async () => (await apiClient.get("/students/me/placement/interviews")).data;
export const getOfferLetters = async () => (await apiClient.get("/students/me/placement/offers")).data;
export const getPlacementTimeline = async () => (await apiClient.get("/students/me/placement/timeline")).data;
export const getApplicationsByStatus = async (status) => (await apiClient.get("/students/me/placement/applications/status", { params: { status } })).data;
export const saveJob = async (jobId) => (await apiClient.post(`/students/me/placement/saved-jobs/${jobId}`)).data;
export const removeSavedJob = async (jobId) => (await apiClient.delete(`/students/me/placement/saved-jobs/${jobId}`)).data;