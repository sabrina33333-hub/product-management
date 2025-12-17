-- 每次啟動時，先清空舊資料，避免重複插入
--DELETE FROM users;

-- 插入三筆新的使用者資料
-- 注意：我們不需要指定 id，因為它被設定為自動遞增
INSERT INTO users (username, password,email) VALUES ('Alice','pasword123456', 'alice@example.com');
INSERT INTO users (username, password,email) VALUES ('Bob', 'ppp456','bob@example.com');
INSERT INTO users (username, password,email) VALUES ('Charlie', 'dddd6666','charlie@example.com');
