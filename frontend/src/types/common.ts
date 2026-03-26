// src/types/common.ts
import { z } from "zod";

export const ApiErrorSchema = z.object({
  status: z.number(),
  message: z.string(),
  timestamp: z.string(),
});

export type ApiError = z.infer<typeof ApiErrorSchema>;
