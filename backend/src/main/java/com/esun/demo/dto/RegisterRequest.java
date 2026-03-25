package com.esun.demo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userName;
    private String phoneNumber;
    private String password;
    private String email;
}