-- 使用者表
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL, -- 登入唯一識別
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    biography TEXT,
    cover_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 貼文表
CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_user FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 留言表
CREATE TABLE comments (
    comment_id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_post FOREIGN KEY(post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ==========================================
-- 3. 建立預存程序 (Stored Procedures)
-- ==========================================

-- A. 註冊使用者程序
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

--  更新使用者
CREATE OR REPLACE PROCEDURE update_user_profile(
    p_phone VARCHAR,
    p_user_name VARCHAR,
    p_password_hash VARCHAR,
    p_biography TEXT,
    p_cover_image VARCHAR -- <--- 新增此參數
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE users 
    SET 
        user_name = COALESCE(p_user_name, user_name),
        password_hash = COALESCE(p_password_hash, password_hash),
        biography = COALESCE(p_biography, biography),
        cover_image = COALESCE(p_cover_image, cover_image) -- <--- 更新此欄位
    WHERE phone_number = p_phone;
END;
$$;

-- B. 新增貼文程序
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

-- C. 更新貼文程序
CREATE OR REPLACE PROCEDURE update_post(
    p_post_id BIGINT,
    p_phone VARCHAR,
    p_content TEXT,
    p_image_url VARCHAR -- 這是輸入參數名稱，可以保留，但建議與資料庫一致
)
LANGUAGE plpgsql AS $$
DECLARE
    v_user_id BIGINT;
BEGIN
    -- 獲取當前操作者的 ID
    SELECT user_id INTO v_user_id FROM users WHERE phone_number = p_phone;

    -- 執行更新
    UPDATE posts 
    SET 
        content = COALESCE(p_content, content),
        image = COALESCE(p_image_url, image), -- 修正：左邊是資料表欄位，右邊是原本的值
        created_at = CURRENT_TIMESTAMP
    WHERE post_id = p_post_id AND user_id = v_user_id;

    -- 權限或存在檢查
    IF NOT FOUND THEN
        RAISE EXCEPTION '貼文不存在或您沒有修改權限';
    END IF;
END;
$$;


-- D. 刪除貼文程序
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

-- 查詢貼文
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
    SELECT p.post_id, p.content, p.image, u.user_name, p.created_at
    FROM posts p
    JOIN users u ON p.user_id = u.user_id
    ORDER BY p.created_at DESC;
END;
$$;





-- E. 新增評論
CREATE OR REPLACE PROCEDURE create_comment(
    p_post_id BIGINT,
    p_phone VARCHAR,
    p_content TEXT
)
LANGUAGE plpgsql AS $$
DECLARE
    v_user_id BIGINT;
BEGIN
    -- 根據手機號碼取得 User ID
    SELECT user_id INTO v_user_id FROM users WHERE phone_number = p_phone;
    
    IF v_user_id IS NULL THEN
        RAISE EXCEPTION '使用者不存在';
    END IF;

    -- 插入資料 (欄位對齊你的 Entity)
    INSERT INTO comments (post_id, user_id, content, created_at)
    VALUES (p_post_id, v_user_id, p_content, CURRENT_TIMESTAMP);
END;
$$;

--修改評論 
CREATE OR REPLACE PROCEDURE update_comment(
    p_comment_id BIGINT,
    p_phone VARCHAR,
    p_content TEXT
)
LANGUAGE plpgsql AS $$
DECLARE
    v_user_id BIGINT;
BEGIN
    -- 取得操作者 ID
    SELECT user_id INTO v_user_id FROM users WHERE phone_number = p_phone;

    -- 執行更新並檢查權限
    UPDATE comments 
    SET 
        content = COALESCE(p_content, content)
        -- 這裡不放 updated_at，因為你的 Entity 裡沒定義
    WHERE comment_id = p_comment_id AND user_id = v_user_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION '評論不存在或您沒有修改權限';
    END IF;
END;
$$;


--刪除評論
CREATE OR REPLACE PROCEDURE delete_comment(
    p_comment_id BIGINT,
    p_phone VARCHAR
)
LANGUAGE plpgsql AS $$
DECLARE
    v_user_id BIGINT;
BEGIN
    SELECT user_id INTO v_user_id FROM users WHERE phone_number = p_phone;

    DELETE FROM comments 
    WHERE comment_id = p_comment_id AND user_id = v_user_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION '刪除失敗：評論不存在或您沒有權限';
    END IF;
END;
$$;


CREATE OR REPLACE FUNCTION get_all_comments()
RETURNS TABLE (
    comment_id BIGINT,
    post_id BIGINT,
    user_id BIGINT,
    content TEXT,
    created_at TIMESTAMP
) 
AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        c.comment_id, 
        c.post_id,
        c.user_id,
        c.content, 
        c.created_at
    FROM comments c
    ORDER BY c.created_at DESC;
END;
$$ LANGUAGE plpgsql;