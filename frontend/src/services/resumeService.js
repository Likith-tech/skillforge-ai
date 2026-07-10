import apiClient from "./apiClient";

const fileNameFromDisposition = (disposition, fallback) => {
  if (!disposition) {
    return fallback;
  }

  const match = disposition.match(/filename="?([^"]+)"?/);
  return match?.[1] || fallback;
};

const resumeFormData = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return formData;
};

export const getResume = async () => {
  const response = await apiClient.get("/resumes/me");
  return response.data;
};

export const uploadResume = async (file) => {
  const response = await apiClient.post("/resumes/me", resumeFormData(file), {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
};

export const replaceResume = async (file) => {
  const response = await apiClient.put("/resumes/me", resumeFormData(file), {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
};

export const deleteResume = async () => {
  await apiClient.delete("/resumes/me");
};

export const getResumeHistory = async () => {
  const response = await apiClient.get("/resumes/me/history");
  return response.data;
};

export const getResumePreviewBlob = async () => {
  const response = await apiClient.get("/resumes/me/preview", { responseType: "blob" });
  return response.data;
};

export const getResumeVersionPreviewBlob = async (versionId) => {
  const response = await apiClient.get(`/resumes/me/versions/${versionId}/preview`, {
    responseType: "blob",
  });
  return response.data;
};

export const downloadResumeBlob = async () => {
  const response = await apiClient.get("/resumes/me/download", { responseType: "blob" });
  return {
    blob: response.data,
    fileName: fileNameFromDisposition(response.headers["content-disposition"], "resume"),
  };
};

export const downloadResumeVersionBlob = async (versionId) => {
  const response = await apiClient.get(`/resumes/me/versions/${versionId}/download`, {
    responseType: "blob",
  });
  return {
    blob: response.data,
    fileName: fileNameFromDisposition(response.headers["content-disposition"], "resume"),
  };
};

export const saveBlob = ({ blob, fileName }) => {
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
};
