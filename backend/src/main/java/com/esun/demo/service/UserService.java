package com.esun.demo.service;

import com.esun.demo.config.JwtUtils;
import com.esun.demo.dto.UpdateProfileRequest;
import com.esun.demo.dto.UserUpdateRequest;
import com.esun.demo.entity.User;
import com.esun.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.nio.file.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 註冊：
    @Transactional
    public String registerUser(String name, String phone, String rawPassword, String email) {
        if (userRepository.existsByPhoneNumber(phone)) {
            return "該手機號碼已註冊";
        }

        // 進行密碼加密
        String encodedPassword = passwordEncoder.encode(rawPassword);

        try {

            userRepository.registerUser(
                    name,
                    phone,
                    encodedPassword,
                    email,
                    null,
                    null);
            return "註冊成功 (via Stored Procedure)";
        } catch (Exception e) {
            return "註冊失敗：" + e.getMessage();
        }
    }

    // 登入
    public String login(String phone, String password) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phone);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return jwtUtils.generateToken(phone);
            }
        }
        return "手機號碼或密碼錯誤";
    }

    // 更新簡介與圖片

    @Transactional
    public String updateFullProfileViaSP(String phone, UpdateProfileRequest req) {
        if (!userRepository.existsByPhoneNumber(phone)) {
            return "使用者不存在";
        }

        // 1. 處理密碼加密
        String encodedPassword = (req.getPassword() != null && !req.getPassword().isEmpty())
                ? passwordEncoder.encode(req.getPassword())
                : null;

        // 2. 處理圖片上傳
        String imagePath = null;
        if (req.getCoverImage() != null && !req.getCoverImage().isEmpty()) {
            try {
                String fileName = phone + "_" + System.currentTimeMillis() + ".jpg";
                Path uploadPath = Paths.get("src/main/resources/static/uploads");
                if (!Files.exists(uploadPath))
                    Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(req.getCoverImage().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = "/uploads/" + fileName;
            } catch (Exception e) {
                return "圖片上傳失敗：" + e.getMessage();
            }
        }

        try {
            userRepository.updateUserProfile(
                    phone,
                    req.getUserName(),
                    encodedPassword,
                    req.getBiography(),
                    imagePath);
            return "個人資料更新成功 (via SP)";
        } catch (Exception e) {
            if (TransactionAspectSupport.currentTransactionStatus() != null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

            return "更新失敗：" + e.getMessage();
        }
    }
}