import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8081",
});

apiClient.interceptors.request.use((config) => {
  config.headers = config.headers || {};
  config.headers["X-Request-Source"] = "skillforge-web";
  return config;
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => {
    if (response.data && Object.prototype.hasOwnProperty.call(response.data, "data")) {
      response.data = response.data.data;
    }

    return response;
  },
  (error) => {
    if (error.response?.data?.message) {
      return Promise.reject(error);
    }

    return Promise.reject(error);
  }
);

export default apiClient;
