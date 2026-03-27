package com.esun.demo.controller;

import com.esun.demo.dto.UpdateProfileRequest;
import com.esun.demo.dto.UserUpdateRequest;
import com.esun.demo.entity.User;
import com.esun.demo.repository.UserRepository;
import com.esun.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public Object getMyProfile() {
        // 從 SecurityContext 抓取剛才在 Filter 存入的手機號碼
        String phone = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 根據手機號碼去資料庫抓完整資料
        Optional<User> user = userRepository.findByPhoneNumber(phone);

        if (user.isPresent()) {
            return user.get(); // 回傳使用者物件
        }
        return "找不到使用者";
    }

    @PutMapping(value = "/update-profile", consumes = { "multipart/form-data" })
    public ResponseEntity<String> updateFullProfile(@ModelAttribute UpdateProfileRequest req) {
        // 從 SecurityContext 取得目前登入者手機
        String phone = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 呼叫 Service
        String result = userService.updateFullProfileViaSP(phone, req);

        return ResponseEntity.ok(result);
    }
}