package com.esun.demo.repository;

import com.esun.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 用於登入查詢（保持使用 JPA 查詢，因為這是 SELECT）
    Optional<User> findByPhoneNumber(String phoneNumber);

    // 檢查手機號碼是否已存在 (新增這一行)
    boolean existsByPhoneNumber(String phoneNumber);

    // 用於註冊（改用預存程序執行 INSERT）
    @Procedure(procedureName = "register_user")
    void registerUser(
            String p_user_name,
            String p_phone,
            String p_password,
            String p_email,
            String p_biography,
            String p_cover_image);

    @Procedure(procedureName = "update_user_profile")
    void updateUserProfile(
            String p_phone,
            String p_user_name,
            String p_password_hash,
            String p_biography,
            String p_cover_image);
}