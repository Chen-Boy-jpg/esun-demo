import { z } from "zod";

// 1. 建立貼文請求 (與前端表單對接)
export const CreatePostRequestSchema = z.object({
  content: z.string().min(1, "內容不能為空").max(500, "內容不能超過 500 字"),
  // 注意：如果是用 FormData 上傳，File 物件通常不在 Zod Schema 驗證文字的部分
});

// 2. 貼文回應單一物件
export const PostResponseSchema = z.object({
  postId: z.number(),
  content: z.string(),
  authorName: z.string(),
  createdAt: z.string(), // 維持字串，方便格式化函數處理
  imageUrl: z.string().nullable().optional(), // 增加 nullable 以應對後端傳回 null 的情況
  commentCount: z.number().optional(), // 預留給我們在 HomeView 縫合後的欄位
});

// 3. 貼文列表
export const PostListResponseSchema = z.array(PostResponseSchema);

// 匯出型別
export type CreatePostRequest = z.infer<typeof CreatePostRequestSchema>;
export type PostResponse = z.infer<typeof PostResponseSchema>;
