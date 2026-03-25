package com.esun.demo.controller;

import com.esun.demo.dto.CommentRequest;
import com.esun.demo.dto.CommentResponse; // 建議回傳封裝好的 DTO
import com.esun.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 1. 列出所有評論 (所有人皆可查看)
    @GetMapping
    public ResponseEntity<List<CommentResponse>> listAll() {
        // 在 Service 裡將 List<Object[]> 轉成 List<CommentResponse>
        return ResponseEntity.ok(commentService.getAllComments());
    }

    // 2. 新增評論 (需登入)
    @PostMapping("/{postId}")
    public ResponseEntity<?> create(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            Authentication authentication) {

        String phone = authentication.getName();
        commentService.addComment(postId, phone, request.getContent());
        return ResponseEntity.ok(Map.of("message", "評論成功"));
    }

    // 3. 修改評論 (需登入且為本人)
    @PutMapping("/{commentId}")
    public ResponseEntity<?> update(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            Authentication authentication) {

        String phone = authentication.getName();
        commentService.editComment(commentId, phone, request.getContent());
        return ResponseEntity.ok(Map.of("message", "修改成功"));
    }

    // 4. 刪除評論 (需登入且為本人)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(
            @PathVariable Long commentId,
            Authentication authentication) {

        String phone = authentication.getName();
        commentService.removeComment(commentId, phone);
        return ResponseEntity.ok(Map.of("message", "刪除成功"));
    }
}