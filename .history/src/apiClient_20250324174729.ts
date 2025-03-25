import axios, { AxiosInstance, AxiosError } from "axios";

const apiClient: AxiosInstance = axios.create({
  baseURL: "http://local",
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
    if (error.response) {
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
          window.location.href = "/";
          break;
      }
    } else if (error.request) {
      console.error(error.request);
      console.log(localStorage.getItem("jwtToken"));
      alert("请求失败，请检查网络连接！");
      localStorage.removeItem("jwtToken");
      //window.location.href = "/";
    } else {
      console.error(error.message);
      alert("请求失败，请稍后重试！");
      localStorage.removeItem("jwtToken");
      window.location.href = "/";
    }

    return Promise.reject(error);
  }
);

export default apiClient;
