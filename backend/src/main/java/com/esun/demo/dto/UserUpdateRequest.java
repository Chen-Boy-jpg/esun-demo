package com.esun.demo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateRequest {
    private String userName;
    private String email;
    private String biography;
    private String password; // 如果有傳入才修改
    private MultipartFile coverImage; // 處理圖片檔案
}