import apiClient from "./apiClient";

export const registerUser = async ({ name, email, password, role }) => {
  const response = await apiClient.post("/auth/register", { name, email, password, role });
  return response.data;
};

export const loginUser = async ({ email, password }) => {
  const response = await apiClient.post("/auth/login", { email, password });
  return response.data;
};
