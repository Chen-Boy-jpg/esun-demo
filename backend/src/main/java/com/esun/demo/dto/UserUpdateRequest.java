package com.esun.demo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateRequest {
    private String userName;
    private String email;
    private String biography;
    private String password;
    private MultipartFile coverImage;
}