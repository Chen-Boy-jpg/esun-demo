import api from "../api";
// 假設你有定義 User 相關的型別，若無可先用 inline interface 或 any
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
   * 使用 FormData 處理 multipart/form-data
   */
  async updateProfile(nickname: string, biography: string, file?: File | null) {
    const formData = new FormData();

    // 1. 根據截圖，Key 應該是 'userName' 而不是 'nickname'
    formData.append("userName", nickname);

    // 2. 這裡維持 'biography'
    formData.append("biography", biography || "");

    // 3. 根據截圖，Key 應該是 'coverImage' 而不是 'file'
    if (file) {
      formData.append("coverImage", file);
    }

    // 4. 確認路徑與 Postman 一致 (剛才截圖對應的是 /users/update-profile)
    const res = await api.put("/user/update-profile", formData);
    return res.data;
  },
};
