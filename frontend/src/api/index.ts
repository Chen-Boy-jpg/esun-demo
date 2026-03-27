import axios, { AxiosError } from "axios";

const api = axios.create({
  baseURL: "/api",
  timeout: 10000,
  headers: { "Content-Type": "application/json" },
});

// 請求攔截器
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // 只有當 data 不是 FormData 時，才預設為 json
  if (!(config.data instanceof FormData)) {
    config.headers["Content-Type"] = "application/json";
  } else {
    // 如果是 FormData，就刪除 Content-Type 讓瀏覽器自動處理
    delete config.headers["Content-Type"];
  }

  return config;
});

// 回應攔截器
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<any>) => {
    const status = error.response?.status;
    const data = error.response?.data;

    if (status === 401) {
      localStorage.removeItem("token");
    }

    const errorMsg = data?.message || "伺服器連線異常";
    return Promise.reject(new Error(errorMsg));
  },
);

export default api;
