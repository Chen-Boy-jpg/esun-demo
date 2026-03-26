-- ==========================================
-- 1. 刪除舊表 (確保初始化環境乾淨)
-- ==========================================
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ==========================================
-- 2. 建立資料表 (使用 BIGSERIAL 避免類型衝突)
-- ==========================================

-- 使用者表
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    biography TEXT,
    cover_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 貼文表
CREATE TABLE posts (
    post_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_user FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 留言表
CREATE TABLE comments (
    comment_id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_post FOREIGN KEY(post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ==========================================
-- 3. 建立預存程序 (Stored Procedures)
-- ==========================================

-- A. 註冊使用者
CREATE OR REPLACE PROCEDURE register_user(
    p_user_name VARCHAR,
    p_phone VARCHAR,
    p_password VARCHAR,
    p_email VARCHAR,
    p_biography TEXT,
    p_cover_image VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO users (user_name, phone_number, password_hash, email, biography, cover_image, created_at)
    VALUES (p_user_name, p_phone, p_password, p_email, p_biography, p_cover_image, CURRENT_TIMESTAMP);
END;
$$;

-- B. 更新使用者個人資料
CREATE OR REPLACE PROCEDURE update_user_profile(
    p_phone VARCHAR,
    p_user_name VARCHAR,
    p_password_hash VARCHAR,
    p_biography TEXT,
    p_cover_image VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE users 
    SET 
        user_name = COALESCE(p_user_name, user_name),
        password_hash = COALESCE(p_password_hash, password_hash),
        biography = COALESCE(p_biography, biography),
        cover_image = COALESCE(p_cover_image, cover_image)
    WHERE phone_number = p_phone;
END;
$$;

-- C. 新增貼文
CREATE OR REPLACE PROCEDURE create_post(
    p_phone VARCHAR,
    p_content TEXT,
    p_image_url VARCHAR
)
LANGUAGE plpgsql AS $$
DECLARE
    v_user_id BIGINT;
BEGIN
    SELECT user_id INTO v_user_id FROM users WHERE phone_number = p_phone;
    
    INSERT INTO posts (user_id, content, image, created_at)
    VALUES (v_user_id, p_content, p_image_url, CURRENT_TIMESTAMP);
END;
$$;

-- D. 更新貼文
CREATE OR REPLACE PROCEDURE update_post(
    p_post_id BIGINT,
    p_phone VARCHAR,
    p_content TEXT,
    p_image_url VARCHAR
)
LANGUAGE plpgsql AS $$
DECLARE
    v_user_id BIGINT;
BEGIN
    SELECT user_id INTO v_user_id FROM users WHERE phone_number = p_phone;

    UPDATE posts 
    SET 
        content = COALESCE(p_content, content),
        image = COALESCE(p_image_url, image),
        created_at = CURRENT_TIMESTAMP
    WHERE post_id = p_post_id AND user_id = v_user_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION '貼文不存在或您沒有修改權限';
    END IF;
END;
$$;

-- E. 刪除貼文
CREATE OR REPLACE PROCEDURE delete_post(
    p_post_id BIGINT,
    p_phone VARCHAR
)
LANGUAGE plpgsql AS $$
DECLARE
    v_user_id BIGINT;
BEGIN
    SELECT user_id INTO v_user_id FROM users WHERE phone_number = p_phone;

    DELETE FROM posts 
    WHERE post_id = p_post_id AND user_id = v_user_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION '刪除失敗：貼文不存在或權限不足';
    END IF;
END;
$$;

-- F. 新增留言
CREATE OR REPLACE PROCEDURE create_comment(
    p_post_id BIGINT,
    p_phone VARCHAR,
    p_content TEXT
)
LANGUAGE plpgsql AS $$
DECLARE
    v_user_id BIGINT;
BEGIN
    SELECT user_id INTO v_user_id FROM users WHERE phone_number = p_phone;
    
    IF v_user_id IS NULL THEN
        RAISE EXCEPTION '使用者不存在';
    END IF;

    INSERT INTO comments (post_id, user_id, content, created_at)
    VALUES (p_post_id, v_user_id, p_content, CURRENT_TIMESTAMP);
END;
$$;

-- ==========================================
-- 4. 建立查詢函數 (Functions)
-- ==========================================

-- 查詢所有貼文
CREATE OR REPLACE FUNCTION get_all_posts()
RETURNS TABLE (
    post_id BIGINT,
    content TEXT,
    image_url VARCHAR,
    author_name VARCHAR,
    created_at TIMESTAMP
) 
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        p.post_id::BIGINT, -- 強制轉型確保與回傳定義一致
        p.content, 
        p.image, 
        u.user_name, 
        p.created_at
    FROM posts p
    JOIN users u ON p.user_id = u.user_id
    ORDER BY p.created_at DESC;
END;
$$;

-- 查詢所有留言
CREATE OR REPLACE FUNCTION get_all_comments()
RETURNS TABLE (
    comment_id BIGINT,
    post_id BIGINT,
    user_id BIGINT,
    content TEXT,
    created_at TIMESTAMP
) 
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        c.comment_id::BIGINT,
        c.post_id::BIGINT,
        c.user_id::BIGINT,
        c.content, 
        c.created_at
    FROM comments c
    ORDER BY c.created_at DESC;
END;
$$;

-- ==========================================
-- 5. 插入測試資料 (使用 DO 區塊確保順序與類型)
-- ==========================================
DO $$
BEGIN
    -- 註冊使用者
    CALL register_user('阿龍', '0911111111', '$2a$10$WO0eT1j6pZHmye35KF8uU.iZdIMLbVO2Hckn51gML4yWCbbo5Gicy', 'along@email.com', '熱愛攝影與生活分享。', NULL);
    CALL register_user('美美', '0922222222', '$2a$10$WO0eT1j6pZHmye35KF8uU.iZdIMLbVO2Hckn51gML4yWCbbo5Gicy', 'meimei@email.com', '前端工程師 / 貓奴', NULL);
    CALL register_user('小傑', '0933333333', '$2a$10$WO0eT1j6pZHmye35KF8uU.iZdIMLbVO2Hckn51gML4yWCbbo5Gicy', 'jay@email.com', 'Java 研習中', NULL);

    -- 新增貼文
    CALL create_post('0911111111', '今天去爬山，空氣真的很好！', NULL);
    CALL create_post('0922222222', 'Vue 寫到少一個括號... Q_Q', NULL);
    CALL create_post('0933333333', '拉麵是工程師的浪漫。', NULL);

    -- 新增評論 (1::BIGINT 確保類型正確)
    CALL create_comment(1::BIGINT, '0922222222', '照片拍得太美了吧！');
    CALL create_comment(2::BIGINT, '0933333333', '我也常幹這種事，拍拍。');
    CALL create_comment(3::BIGINT, '0911111111', '這家在哪？求店名！');

EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE '初始化資料出錯: %', SQLERRM;
END $$;