package com.esun.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
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
    private String phoneNumber;
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String biography;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "cover_image")
    private String coverImage;
}