import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  const token =
    localStorage.getItem("token") ||
    localStorage.getItem("jwt") ||
    localStorage.getItem("authToken");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export const getStudentProfile = async () => {
  const response = await api.get("/student/profile");
  return response.data;
};

export const createStudentProfile = async (profile) => {
  const response = await api.post("/student/profile", profile);
  return response.data;
};

export const updateStudentProfile = async (profile) => {
  const response = await api.put("/student/profile", profile);
  return response.data;
};
