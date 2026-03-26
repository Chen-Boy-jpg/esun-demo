import { ref } from "vue";
import api from "../api";
import type {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
} from "../types/auth";

// 建立一個響應式的狀態，初始值讀取 localStorage
// 放在 export 外面作為私有變數，確保單一數據源
const _isAuthenticated = ref(!!localStorage.getItem("token"));

export const AuthService = {
  /**
   * 使用者登入
   */
  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      // 這裡假設後端回傳的是 AuthResponse 格式
      const res = await api.post<AuthResponse>("/auth/login", data);

      if (res.data && res.data.token) {
        // 1. 存入 LocalStorage
        localStorage.setItem("token", res.data.token);

        // 2. 存入使用者名稱 (如果後端有回傳的話)
        if (res.data.userName) {
          localStorage.setItem("userName", res.data.userName);
        }

        // 3. 【關鍵】更新響應式狀態，觸發 Navbar 更新
        _isAuthenticated.value = true;
      }

      return res.data;
    } catch (error: any) {
      // 統一處理錯誤噴出，讓 LoginView 能 catch 到
      throw error.response?.data || error.message || "登入失敗";
    }
  },

  /**
   * 使用者註冊
   */
  async register(data: RegisterRequest): Promise<AuthResponse> {
    // 解構掉不需要傳給後端的 confirmPassword
    const { confirmPassword, ...registerData } = data;
    try {
      const res = await api.post<AuthResponse>("/auth/register", registerData);
      return res.data;
    } catch (error: any) {
      throw error.response?.data || error.message || "註冊失敗";
    }
  },

  /**
   * 登出系統
   */
  logout(): void {
    // 1. 清除所有相關的 LocalStorage
    localStorage.removeItem("token");
    localStorage.removeItem("userName");

    // 2. 【關鍵】更新響應式狀態為 false
    _isAuthenticated.value = false;

    // 3. 跳轉頁面 (使用 window.location 可以徹底重置應用狀態，或是改用 router.push)
    window.location.href = "/login";
  },

  /**
   * 檢查目前是否為登入狀態 (響應式)
   */
  isAuthenticated(): boolean {
    return _isAuthenticated.value;
  },

  /**
   * 獲取目前登入的使用者名稱
   */
  getUserName(): string | null {
    return localStorage.getItem("userName");
  },

  /**
   * 獲取目前 Token (供 axios interceptor 使用)
   */
  getToken(): string | null {
    return localStorage.getItem("token");
  },
};
