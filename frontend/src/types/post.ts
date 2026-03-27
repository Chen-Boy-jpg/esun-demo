import { z } from "zod";

//  建立貼文請求 (與前端表單對接)
export const CreatePostRequestSchema = z.object({
  content: z.string().min(1, "內容不能為空").max(500, "內容不能超過 500 字"),
});

//  貼文回應單一物件
export const PostResponseSchema = z.object({
  postId: z.number(),
  content: z.string(),
  authorName: z.string(),
  createdAt: z.string(),
  imageUrl: z.string().nullable().optional(),
  commentCount: z.number().optional(),
});

//  貼文列表
export const PostListResponseSchema = z.array(PostResponseSchema);

// 匯出型別
export type CreatePostRequest = z.infer<typeof CreatePostRequestSchema>;
export type PostResponse = z.infer<typeof PostResponseSchema>;
