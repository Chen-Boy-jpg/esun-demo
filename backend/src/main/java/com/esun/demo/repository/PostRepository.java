package com.esun.demo.repository;

import com.esun.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 新增貼文
    @Procedure(procedureName = "create_post")
    void createPost(String p_phone, String p_content, String p_image_url);

    // 修改貼文
    @Procedure(procedureName = "update_post")
    void updatePost(Long p_post_id, String p_phone, String p_content, String p_image_url);

    // 刪除貼文
    @Procedure(procedureName = "delete_post")
    void deletePost(Long p_post_id, String p_phone);

    // 列出貼文 (使用剛才建立的 Function)
    @Query(value = "SELECT * FROM get_all_posts()", nativeQuery = true)
    List<Object[]> findAllPostsViaFunction();
}