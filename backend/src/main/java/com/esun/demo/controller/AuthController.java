package com.esun.demo.controller;

import com.esun.demo.dto.LoginRequest;
import com.esun.demo.dto.RegisterRequest;
import com.esun.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest req) {
        // 直接調用，如果失敗（如重複註冊），Service 會拋出 Exception
        String result = userService.registerUser(
                req.getUserName(),
                req.getPhoneNumber(),
                req.getPassword(),
                req.getEmail());

        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest req) {

        String token = userService.login(req.getPhone(), req.getPassword());

        return ResponseEntity.ok(Map.of(
                "message", "登入成功",
                "token", token));
    }

}