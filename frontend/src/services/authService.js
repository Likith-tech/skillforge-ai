import apiClient from "./apiClient";

export const registerUser = async ({ name, email, password, role }) => {
  const response = await apiClient.post("/auth/register", { name, email, password, role });
  return response.data;
};

export const loginUser = async ({ email, password }) => {
  const response = await apiClient.post("/auth/login", { email, password });
  return response.data;
};

export const logoutUser = async (refreshToken) => {
  if (!refreshToken) {
    return;
  }
  await apiClient.post("/auth/logout", { refreshToken });
};

export const refreshAccessToken = async (refreshToken) => {
  const response = await apiClient.post("/auth/refresh", { refreshToken });
  return response.data;
};

export const requestPasswordReset = async (email) => {
  const response = await apiClient.post("/auth/forgot-password", { email });
  return response.data;
};

export const resetPassword = async ({ resetToken, newPassword, confirmPassword }) => {
  const response = await apiClient.post("/auth/reset-password", {
    resetToken,
    newPassword,
    confirmPassword,
  });
  return response.data;
};
