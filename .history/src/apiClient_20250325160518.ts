import axios, { AxiosInstance, AxiosError } from "axios";

const apiClient: AxiosInstance = axios.create({
  baseURL: "http://localhost:8080",
});

apiClient.interceptors.request.use(
  (config) => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (jwtToken) {
      config.headers["Authorization"] = jwtToken;
    }
    return config;
  },
  (error) => {
    console.error("发送qinerror);
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
      console.log("error, response: ", status);
      switch (status) {
        case 401:
          alert("登录已过期，请重新登录！");
          //localStorage.removeItem("jwtToken");
          window.location.href = "/";
          break;
        default:
          alert("发生错误，请稍后重试！");
          //localStorage.removeItem("jwtToken");
          window.location.href = "/";
          break;
      }
    } else if (error.request) {
      console.debug("请求 URLbase:", apiClient.defaults.baseURL);
      //localStorage.removeItem("jwtToken");
      //window.location.href = "/";
    } else {
      console.error(error.message);
      alert("请求失败，请稍后重试！");
      //localStorage.removeItem("jwtToken");
      window.location.href = "/";
    }

    return Promise.reject(error);
  }
);

export default apiClient;
