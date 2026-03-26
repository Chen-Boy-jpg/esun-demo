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