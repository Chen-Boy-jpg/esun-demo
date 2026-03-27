import api from "../api";
export interface UserProfile {
  userId?: number;
  userName: string;
  phoneNumber: string;
  email?: string;
  biography?: string;
  coverImage?: string;
}

export const UserService = {
  /**
   * 取得目前登入使用者的詳細資訊
   */
  async getCurrentUser(): Promise<UserProfile> {
    const res = await api.get<UserProfile>("/user/me");
    return res.data;
  },

  /**
   * 更新個人資料 (含圖片上傳)
   */
  async updateProfile(nickname: string, biography: string, file?: File | null) {
    const formData = new FormData();

    formData.append("userName", nickname);

    formData.append("biography", biography || "");

    if (file) {
      formData.append("coverImage", file);
    }

    const res = await api.put("/user/update-profile", formData);
    return res.data;
  },
};
