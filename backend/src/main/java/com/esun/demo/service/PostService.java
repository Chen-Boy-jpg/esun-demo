package com.esun.demo.service;

import com.esun.demo.dto.PostRequest;
import com.esun.demo.dto.PostResponse;
import com.esun.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    /**
     * 取得所有貼文 (呼叫 Database Function)
     */
    public List<PostResponse> getAllPosts() {
        List<Object[]> results = postRepository.findAllPostsViaFunction();

        return results.stream().map(row -> {
            // 處理時間轉型問題
            LocalDateTime createdAt;
            Object dateObj = row[4];

            if (dateObj instanceof java.sql.Timestamp) {

                createdAt = ((java.sql.Timestamp) dateObj).toLocalDateTime();
            } else if (dateObj instanceof java.time.LocalDateTime) {

                createdAt = (LocalDateTime) dateObj;
            } else {

                createdAt = LocalDateTime.now();
            }

            return new PostResponse(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    (String) row[2],
                    (String) row[3],
                    createdAt);
        }).collect(Collectors.toList());
    }

    /**
     * 新增貼文 (呼叫 Stored Procedure)
     */
    @Transactional
    public String createNewPost(String phone, PostRequest req) {
        // 處理圖片存檔
        String imagePath = null;
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            try {
                imagePath = saveImage(req.getImage());
            } catch (Exception e) {
                // 如果圖片儲存失敗
                return "圖片儲存失敗：" + e.getMessage();
            }
        }

        // 呼叫資料庫預存程序
        try {
            postRepository.createPost(phone, req.getContent(), imagePath);
            return "貼文發布成功";
        } catch (Exception e) {

            if (TransactionAspectSupport.currentTransactionStatus() != null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            return "發布失敗：" + e.getMessage();
        }
    }

    /**
     * 更新貼文 (呼叫 Stored Procedure)
     */
    @Transactional
    public String updateExistingPost(Long postId, String phone, PostRequest req) {
        String imagePath = null;

        // 如果有傳新圖片才更新
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            imagePath = saveImage(req.getImage());
        }

        postRepository.updatePost(postId, phone, req.getContent(), imagePath);
        return "更新成功";
    }

    /**
     * 刪除貼文 (呼叫 Stored Procedure)
     */
    @Transactional
    public String deletePost(Long postId, String phone) {
        try {
            postRepository.deletePost(postId, phone);
            return "貼文已刪除";
        } catch (Exception e) {
            return "刪除失敗：" + e.getMessage();
        }
    }

    /**
     * 處理圖片存檔並回傳相對路徑
     */
    private String saveImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {

            Path uploadPath = Paths.get("src/main/resources/static/uploads/posts").toAbsolutePath();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 檔名生成
            String fileName = UUID.randomUUID().toString() + ".png";
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/posts/" + fileName;

        } catch (IOException e) {

            throw new RuntimeException("儲存失敗: " + e.getMessage());
        }
    }
}