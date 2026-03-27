package com.esun.demo.controller;

import com.esun.demo.dto.PostRequest;
import com.esun.demo.dto.PostResponse;
import com.esun.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 取得所有貼文 (公開接口)
     * GET /api/posts/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<PostResponse>> listAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    /**
     * 發布新貼文 (需要 Token)
     * POST /api/posts/create
     */
    @PostMapping(value = "/create", consumes = { "multipart/form-data" })
    public ResponseEntity<String> createPost(@ModelAttribute PostRequest req) {
        // 從 JWT Token 中取得當前登入使用者的手機號碼
        String phone = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String result = postService.createNewPost(phone, req);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新貼文 (需要 Token + 擁有者權限)
     * PUT /api/posts/{id}
     */
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<String> updatePost(
            @PathVariable Long id,
            @ModelAttribute PostRequest req) {

        String phone = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String result = postService.updateExistingPost(id, phone, req);
        return ResponseEntity.ok(result);
    }

    /**
     * 刪除貼文 (需要 Token + 擁有者權限)
     * DELETE /api/posts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        String phone = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String result = postService.deletePost(id, phone);
        return ResponseEntity.ok(result);
    }
}