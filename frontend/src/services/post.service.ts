import api from "../api";
import type { PostResponse } from "../types/post";

export const PostService = {
  /**
   * 取得所有貼文列表
   */
  async getAllPosts(): Promise<PostResponse[]> {
    const res = await api.get<PostResponse[]>("/posts/list");
    return res.data || [];
  },

  /**
   * 根據 ID 取得單一貼文詳情
   */
  async getPostById(id: number): Promise<PostResponse> {
    const res = await api.get<PostResponse>(`/posts/${id}`);
    return res.data;
  },

  /**
   * 發佈新貼文
   */
  async createPost(data: { content: string; image?: File }) {
    const formData = new FormData();
    formData.append("content", data.content);
    if (data.image) {
      formData.append("image", data.image);
    }

    const res = await api.post<string>("/posts/create", formData);
    return res.data;
  },

  /**
   * 刪除貼文
   */
  async deletePost(id: number): Promise<void> {
    await api.delete(`/posts/${id}`);
  },

  /**
   * 更新現有貼文 (包含文字與圖片)
   * @param id 貼文 ID
   * @param data 包含 content 和可選 image 的物件
   */
  updateExistingPost: async (
    id: number | string,
    content: string,
    file?: File | null,
  ) => {
    const formData = new FormData();
    formData.append("content", content);

    if (file) {
      formData.append("image", file);
    }

    const response = await api.put(`/posts/${id}`, formData);
    return response.data;
  },
};
