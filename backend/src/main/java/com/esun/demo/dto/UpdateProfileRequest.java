package com.esun.demo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile; // 必須引入這個套件

@Data
public class UpdateProfileRequest {
    private String userName; // 更新姓名
    private String password; // 更新密碼
    private String biography; // 更新簡介
    private MultipartFile coverImage; // 新增：用於接收前端傳來的圖片檔案
}