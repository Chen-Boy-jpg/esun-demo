package com.esun.demo.entity;

import jakarta.persistence.*;
import lombok.Data; // 如果沒裝 Lombok，請手動寫 Getter/Setter
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String phoneNumber; // 核心：手機號碼註冊

    private String email;

    @Column(nullable = false)
    private String passwordHash; // 加密後的密碼

    private String biography;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "cover_image") // 指定對應到資料庫的 cover_image 欄位
    private String coverImage;
}