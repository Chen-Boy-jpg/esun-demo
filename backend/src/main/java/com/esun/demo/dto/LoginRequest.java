package com.esun.demo.dto;

import lombok.Data;

@Data // 如果沒用 Lombok，請手動產生 Getter/Setter
public class LoginRequest {
    private String phone;
    private String password;
}