// src/types/index.ts
export * from "./auth";
export * from "./post";
export * from "./comment";

// 你也可以在這裡定義全域錯誤格式
export interface ApiError {
  status: number;
  message: string;
  timestamp: string;
}
