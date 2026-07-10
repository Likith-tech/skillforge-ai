import apiClient from "./apiClient";

export const getAdminDashboard = async () => (await apiClient.get("/enterprise/admin/dashboard")).data;
export const getAuditLogs = async () => (await apiClient.get("/enterprise/admin/audit-logs")).data;
export const getNotifications = async (status) => (await apiClient.get("/enterprise/notifications", { params: { status } })).data;
export const getUnreadNotificationCount = async () => (await apiClient.get("/enterprise/notifications/unread-count")).data;
export const markNotificationRead = async (notificationId) => (await apiClient.put(`/enterprise/notifications/${notificationId}/read`)).data;
export const deleteNotification = async (notificationId) => (await apiClient.delete(`/enterprise/notifications/${notificationId}`)).data;
export const searchGlobal = async (q) => (await apiClient.get("/enterprise/search", { params: { q } })).data;
export const getFiles = async () => (await apiClient.get("/enterprise/files")).data;
export const deleteFile = async (fileId) => (await apiClient.delete(`/enterprise/files/${fileId}`)).data;
export const uploadPhoto = async (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return (await apiClient.post("/enterprise/files/photo", formData, { headers: { "Content-Type": "multipart/form-data" } })).data;
};
export const uploadCompanyLogo = async (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return (await apiClient.post("/enterprise/files/company-logo", formData, { headers: { "Content-Type": "multipart/form-data" } })).data;
};
export const downloadReport = async (path) => (await apiClient.get(path, { responseType: "blob" })).data;