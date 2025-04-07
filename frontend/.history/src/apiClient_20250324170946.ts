import axios, { AxiosInstance } from "axios";

const apiClient: AxiosInstance = axios.create({
  baseURL: "https://api.example.com",
  timeout: 5000,
});

apiClient.interceptors.request.use(
  (config) => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (jwtToken) {
      config.headers.Authorization = `Bearer ${jwtToken}`;
    }
    return config;
  },
  (error) => {
    console.error(error);
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error: AxiosError) => {
    const status = error.response.status;

    switch (status) {
      case 401:
        alert("登录已过期，请重新登录！");
        localStorage.removeItem("jwtToken");
        window.location.href = "/";
        break;
      default:
        alert("发生错误，请稍后重试！");  
        localStorage.removeItem("jwtToken");
        window.loc
  }
);
