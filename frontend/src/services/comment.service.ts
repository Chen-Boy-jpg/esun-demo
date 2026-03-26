import api from "../api";
import type { CreateCommentRequest, CommentResponse } from "../types/comment";

export const CommentService = {
  /**
   * 取得特定貼文的所有留言
   * @param postId 貼文 ID
   */
  async getAllComments(): Promise<CommentResponse[]> {
    const res = await api.get<CommentResponse[]>(`/comments`);
    return res.data;
  },

  /**
   * 在特定貼文下新增留言
   * @param postId 貼文 ID
   * @param data 留言內容
   */
  async addComment(
    postId: number,
    data: CreateCommentRequest,
  ): Promise<CommentResponse> {
    const res = await api.post<CommentResponse>(`/comments/${postId}`, data);
    return res.data;
  },

  /**
   * 刪除留言
   * @param commentId 留言 ID
   */
  async deleteComment(commentId: number): Promise<void> {
    await api.delete(`/comments/${commentId}`);
  },
};
