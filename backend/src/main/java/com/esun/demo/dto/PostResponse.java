package com.esun.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PostResponse 用於將貼文資訊（包含作者名稱與時間）回傳給前端。
 * 這裡不回傳 user_id，而是回傳處理過的作者姓名，對前端更友善。
 */
@Data
@NoArgsConstructor // 無參數建構子 (JPA/Jackson 序列化需要)
@AllArgsConstructor // 全參數建構子 (供 Service 層 new 物件使用)
public class PostResponse {

    private Long postId; // 貼文 ID
    private String content; // 貼文內容
    private String imageUrl; // 貼文圖片的相對路徑 (例如: /uploads/posts/xxx.jpg)
    private String authorName; // 作者姓名 (從 users 表 JOIN 過來的)
    private LocalDateTime createdAt; // 發布時間

}