package com.esun.demo.exception;

import com.esun.demo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 專門處理資料庫預存程序噴出的異常 (例如：無權限、找不到資料)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DataAccessException ex) {
        // 取得最底層的錯誤訊息 (通常包含 RAISE EXCEPTION 的內容)
        String fullMessage = ex.getMostSpecificCause().getMessage();

        // 簡單過濾掉 PostgreSQL 的前綴，只留下你寫在 Procedure 裡的文字
        String cleanMessage = fullMessage.split("\n")[0];

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "資料庫操作失敗: " + cleanMessage,
                System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 處理參數校驗失敗、或找不到路徑等一般異常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "伺服器發生未預期錯誤: " + ex.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}