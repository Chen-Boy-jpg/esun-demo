package com.esun.demo.repository;

import com.esun.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

        // 1. 新增評論
        @Procedure(procedureName = "create_comment")
        void createComment(
                        @Param("p_post_id") Long postId,
                        @Param("p_phone") String phone,
                        @Param("p_content") String content);

        // 2. 修改評論
        @Procedure(procedureName = "update_comment")
        void updateComment(
                        @Param("p_comment_id") Long commentId,
                        @Param("p_phone") String phone,
                        @Param("p_content") String content);

        // 3. 刪除評論
        @Procedure(procedureName = "delete_comment")
        void deleteComment(
                        @Param("p_comment_id") Long commentId,
                        @Param("p_phone") String phone);

        @Query(value = "SELECT * FROM get_all_comments()", nativeQuery = true)
        List<Comment> findAllCommentsNative();
}