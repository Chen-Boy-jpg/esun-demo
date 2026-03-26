import { z } from "zod";

// 建立留言請求
export const CreateCommentRequestSchema = z.object({
  content: z.string().min(1, "留言內容不能為空").max(500, "留言字數過多"),
});

// 留言回應物件
export const CommentResponseSchema = z.object({
  id: z.number(),
  content: z.string(),
  authorName: z.string(),
  postId: z.number(),
  createdAt: z.string(),
});

export type CreateCommentRequest = z.infer<typeof CreateCommentRequestSchema>;
export type CommentResponse = z.infer<typeof CommentResponseSchema>;
