import { z } from "zod";

// 登入請求驗證
export const LoginRequestSchema = z.object({
  phone: z.string().min(10, "手機號碼格式不正確").max(15),
  password: z.string().min(6, "密碼至少需要 6 個字元"),
});

// 註冊請求驗證
export const RegisterRequestSchema = z
  .object({
    userName: z.string().min(2, "用戶名至少 2 個字元").max(20),
    phoneNumber: z.string().min(10, "手機號碼格式不正確"),
    email: z.string().email("電子信箱格式不正確"),
    password: z.string().min(6, "密碼至少需要 6 個字元"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "兩次輸入的密碼不一致",
    path: ["confirmPassword"],
  });

//JWT Token回應驗證
export const AuthResponseSchema = z.object({
  token: z.string(),
  message: z.string().optional(),
  userName: z.string().optional(),
});

export type LoginRequest = z.infer<typeof LoginRequestSchema>;
export type RegisterRequest = z.infer<typeof RegisterRequestSchema>;
export type AuthResponse = z.infer<typeof AuthResponseSchema>;
