import apiClient from "./apiClient";

export const getStudentProfile = async () => {
  const response = await apiClient.get("/student/profile");
  return response.data;
};

export const createStudentProfile = async (profile) => {
  const response = await apiClient.post("/student/profile", profile);
  return response.data;
};

export const updateStudentProfile = async (profile) => {
  const response = await apiClient.put("/student/profile", profile);
  return response.data;
};
