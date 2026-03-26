import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // 當前端呼叫 /api 時，自動轉發到後端的 8080
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        // 如果你的後端 Controller 已經有 /api 前綴，就不用 rewrite
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
    },
  },
});
