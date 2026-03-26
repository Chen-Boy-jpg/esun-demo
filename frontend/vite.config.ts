import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],

  server: {
    host: true, // 確保 Docker 外部可以訪問
    port: 5173,
    proxy: {
      // 當前端呼叫 /api 時，自動轉發到 Docker 網路內的後端容器
      "/api": {
        target: "http://backend:8080",
        changeOrigin: true,
      },
      "/uploads": { target: "http://backend:8080" },
    },
  },
});
