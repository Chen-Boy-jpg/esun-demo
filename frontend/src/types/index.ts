export * from "./auth";
export * from "./post";
export * from "./comment";

export interface ApiError {
  status: number;
  message: string;
  timestamp: string;
}
