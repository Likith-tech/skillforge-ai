import apiClient from "./apiClient";

export const getUserSettings = async () => {
  const response = await apiClient.get("/user/settings");
  return response.data;
};

export const updateUserSettings = async (settings) => {
  const response = await apiClient.put("/user/settings", settings);
  return response.data;
};

export const changePassword = async ({ currentPassword, newPassword, confirmPassword }) => {
  const response = await apiClient.put("/user/change-password", {
    currentPassword,
    newPassword,
    confirmPassword,
  });
  return response.data;
};
