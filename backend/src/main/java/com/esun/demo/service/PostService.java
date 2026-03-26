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

    // 定義上傳路徑 (存放在專案根目錄下的 uploads/posts)
    private final String UPLOAD_DIR = "uploads/posts/";

    /**
     * 1. 取得所有貼文 (呼叫 Database Function)
     */
    public List<PostResponse> getAllPosts() {
        List<Object[]> results = postRepository.findAllPostsViaFunction();

        return results.stream().map(row -> {
            // 處理時間轉型問題
            LocalDateTime createdAt;
            Object dateObj = row[4];

            if (dateObj instanceof java.sql.Timestamp) {
                // 如果驅動程式回傳的是舊版的 Timestamp
                createdAt = ((java.sql.Timestamp) dateObj).toLocalDateTime();
            } else if (dateObj instanceof java.time.LocalDateTime) {
                // 如果驅動程式回傳的是新版的 LocalDateTime (你目前的情況)
                createdAt = (LocalDateTime) dateObj;
            } else {
                // 萬一發生意外（例如是 OffsetDateTime 或其他類型）
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
     * 2. 新增貼文 (呼叫 Stored Procedure)
     */
    @Transactional
    public String createNewPost(String phone, PostRequest req) {
        // 1. 處理圖片存檔 (把檢查邏輯封裝在 saveImage 或直接在這裡判斷)
        String imagePath = null;
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            try {
                imagePath = saveImage(req.getImage());
            } catch (Exception e) {
                // 如果圖片儲存失敗，可以選擇直接回傳錯誤
                return "圖片儲存失敗：" + e.getMessage();
            }
        }

        // 2. 呼叫資料庫預存程序
        try {
            postRepository.createPost(phone, req.getContent(), imagePath);
            return "貼文發布成功";
        } catch (Exception e) {
            // ⭐ 重要：手動標記事務回滾，避免 Transaction 狀態衝突導致 500 錯誤
            if (TransactionAspectSupport.currentTransactionStatus() != null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            return "發布失敗：" + e.getMessage();
        }
    }

    /**
     * 3. 更新貼文 (呼叫 Stored Procedure)
     */
    @Transactional
    public String updateExistingPost(Long postId, String phone, PostRequest req) {
        String imagePath = null;

        // 如果有傳新圖片才更新，否則傳 null 給 SP (SP 內會用 COALESCE 處理)
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            imagePath = saveImage(req.getImage());
        }

        postRepository.updatePost(postId, phone, req.getContent(), imagePath);
        return "更新成功";
    }

    /**
     * 4. 刪除貼文 (呼叫 Stored Procedure)
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
     * 輔助方法：處理圖片存檔並回傳相對路徑
     */
    /**
     * 輔助方法：處理圖片存檔並回傳相對路徑
     * 改良點：1. 僅保留副檔名避免編碼問題 2. 確保異常處理完全閉合
     */
    private String saveImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {

            // 如果你一定要用目前的相對路徑，請確保資料夾存在
            Path uploadPath = Paths.get("src/main/resources/static/uploads/posts").toAbsolutePath();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 3. 檔名生成
            String fileName = UUID.randomUUID().toString() + ".png";
            Path filePath = uploadPath.resolve(fileName);

            // 4. 只有確定 file 不為 null 才會執行 getInputStream()
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 5. 回傳對應 WebConfig 映射的網址路徑
            return "/uploads/posts/" + fileName;

        } catch (IOException e) {
            // 拋出 RuntimeException 會觸發 Service 層的事務回滾
            throw new RuntimeException("儲存失敗: " + e.getMessage());
        }
    }
}