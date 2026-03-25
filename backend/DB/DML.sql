-- ==========================================
-- 1. 插入測試使用者 (Users)
-- 密碼部分：假設你的程式使用 BCrypt，這裡提供預設加密後的字串
-- 測試帳號 1: 0912345678 / password123
-- 測試帳號 2: 0987654321 / password123
-- ==========================================

INSERT INTO users (user_name, phone_number, password_hash, email, biography, cover_image)
VALUES 
('玉山小襄', '0912345678', '$2a$10$7p6Xp6Xp6Xp6Xp6Xp6Xp6uR8X1X1X1X1X1X1X1X1X1X1X1X1X1X1X', 'hc@esunbank.com.tw', '熱愛 Java 開發的工程師', 'cover_01.jpg'),
('金融小金', '0987654321', '$2a$10$7p6Xp6Xp6Xp6Xp6Xp6Xp6uR8X1X1X1X1X1X1X1X1X1X1X1X1X1X1X', 'gold@esunbank.com.tw', '理財專家', 'cover_02.jpg')
ON CONFLICT (phone_number) DO NOTHING;

-- ==========================================
-- 2. 插入測試貼文 (Posts)
-- 這裡對應剛剛創立的使用者 ID (1 和 2)
-- ==========================================

INSERT INTO posts (user_id, content, image, created_at)
VALUES 
(1, '大家好！這是我的第一篇社群貼文，使用的是 Spring Boot 加上 PostgreSQL 預存程序開發。', 'post_01.jpg', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(2, '今天玉山銀行的理財講座非常精彩，收穫滿滿！', 'post_02.jpg', CURRENT_TIMESTAMP - INTERVAL '2 hours');

-- ==========================================
-- 3. 插入測試留言 (Comments)
-- 這裡對應貼文 ID 與使用者 ID
-- ==========================================

INSERT INTO comments (post_id, user_id, content, created_at)
VALUES 
(1, 2, '寫得太棒了！預存程序在金融系統真的很重要。', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
(2, 1, '我也想參加，下次有講座請記得分享！', CURRENT_TIMESTAMP - INTERVAL '5 minutes');

-- ==========================================
-- 4. 驗證資料的 SQL (僅供參考，不需放入 data.sql)
-- ==========================================
-- SELECT u.user_name, p.content, c.content as comment_content
-- FROM users u
-- JOIN posts p ON u.user_id = p.user_id
-- JOIN comments c ON p.post_id = c.post_id;