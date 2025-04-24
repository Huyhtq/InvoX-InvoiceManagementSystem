INSERT INTO app_user (username, password, access_level)
VALUES ('admin', 'abc1234', 4);

delete from app_user where username = 'admin';