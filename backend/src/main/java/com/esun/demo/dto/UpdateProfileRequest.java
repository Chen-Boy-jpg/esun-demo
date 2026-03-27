package com.esun.demo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateProfileRequest {
    private String userName;
    private String password;
    private String biography;
    private MultipartFile coverImage;
}