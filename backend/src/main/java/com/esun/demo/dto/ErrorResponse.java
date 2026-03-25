package com.esun.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status; // HTTP 狀態碼 (如 400, 403, 500)
    private String message; // 給前端看的錯誤訊息
    private long timestamp; // 錯誤發生的時間戳
}