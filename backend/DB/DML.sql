-- ==========================================
-- 1. 新增 3 位測試使用者 (使用你的 Procedure)
-- ==========================================
-- 密碼統一設為 'password123' (實際開發建議由後端加密，這裡僅作資料填充)
CALL register_user('阿龍', '0911111111', 'password123', 'along@email.com', '熱愛攝影與生活分享。', '/uploads/avatars/user1.jpg');
CALL register_user('美美', '0922222222', 'password123', 'meimei@email.com', '前端工程師 / 貓奴 / 甜點愛好者', '/uploads/avatars/user2.jpg');
CALL register_user('小傑', '0933333333', 'password123', 'jay@email.com', '打球、追劇、寫 Java', '/uploads/avatars/user3.jpg');

-- ==========================================
-- 2. 新增 5 則測試貼文 (模擬不同情境)
-- ==========================================

-- 阿龍發布：風景圖
CALL create_post('0911111111', '今天去爬山，空氣真的很好！推薦大家假日多出來走走。', '/uploads/posts/mountain.jpg');

-- 美美發布：工作心情 (純文字)
CALL create_post('0922222222', '今天寫 Vue 遇到一個卡很久的 Bug，最後發現竟然是少寫一個括號... 大家寫 Code 要細心點 Q_Q', NULL);

-- 小傑發布：食物圖
CALL create_post('0933333333', '下班後來一碗拉麵，這就是工程師的浪漫。', '/uploads/posts/ramen.jpg');

-- 阿龍發布：攝影作品
CALL create_post('0911111111', '分享一張昨天拍到的夕陽，無濾鏡直出。', '/uploads/posts/sunset.jpg');

-- 美美發布：活動宣傳
CALL create_post('0922222222', '這週末有人要一起去技術研討會嗎？這屆的講師陣容很強喔！', '/uploads/posts/conf.jpg');

-- ==========================================
-- 3. 新增測試留言 (模擬互動)
-- ==========================================

-- 假設剛剛生成的 Post ID 依序是 1, 2, 3, 4, 5
-- 美美回覆阿龍的山景圖 (Post ID: 1)
CALL create_comment(1, '0922222222', '照片拍得太美了吧！');

-- 小傑回覆美美的 Bug 貼文 (Post ID: 2)
CALL create_comment(2, '0933333333', '我也常幹這種事，拍拍（握手）。');

-- 阿龍回覆小傑的拉麵 (Post ID: 3)
CALL create_comment(3, '0911111111', '這家在哪？求店名！');

-- 小傑回覆自己的拉麵 (Post ID: 3)
CALL create_comment(3, '0933333333', '在捷運中山站附近，私訊你！');