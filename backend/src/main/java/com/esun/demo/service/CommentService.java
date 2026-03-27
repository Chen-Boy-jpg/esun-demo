package com.esun.demo.service;

import com.esun.demo.dto.CommentResponse;
import com.esun.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.esun.demo.entity.Comment;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public void addComment(Long postId, String phone, String content) {
        commentRepository.createComment(postId, phone, content);
    }

    @Transactional
    public void editComment(Long commentId, String phone, String content) {
        commentRepository.updateComment(commentId, phone, content);
    }

    @Transactional
    public void removeComment(Long commentId, String phone) {
        commentRepository.deleteComment(commentId, phone);
    }

    public List<CommentResponse> getAllComments() {

        List<Comment> results = commentRepository.findAllCommentsNative();

        return results.stream().map(comment -> new CommentResponse(
                comment.getCommentId(),
                comment.getPost().getPostId(),
                comment.getUser().getUserId(),
                comment.getContent(),
                comment.getCreatedAt())).toList();
    }
}