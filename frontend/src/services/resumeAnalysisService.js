import apiClient from "./apiClient";

export const getResumeAnalysisDashboard = async () => {
  const response = await apiClient.get("/resumes/me/analysis");
  return response.data;
};

export const getResumeAnalysisParsed = async () => {
  const response = await apiClient.get("/resumes/me/analysis/parsed");
  return response.data;
};

export const getResumeAnalysisScores = async () => {
  const response = await apiClient.get("/resumes/me/analysis/scores");
  return response.data;
};

export const getResumeAnalysisKeywords = async () => {
  const response = await apiClient.get("/resumes/me/analysis/keywords");
  return response.data;
};

export const getResumeAnalysisSuggestions = async () => {
  const response = await apiClient.get("/resumes/me/analysis/suggestions");
  return response.data;
};

export const getResumeAnalysisAnalytics = async () => {
  const response = await apiClient.get("/resumes/me/analysis/analytics");
  return response.data;
};

export const getResumeAnalysisHistory = async () => {
  const response = await apiClient.get("/resumes/me/analysis/history");
  return response.data;
};

export const compareResumeAnalysisVersions = async (leftVersionId, rightVersionId) => {
  const response = await apiClient.get("/resumes/me/analysis/history/compare", {
    params: { leftVersionId, rightVersionId },
  });
  return response.data;
};