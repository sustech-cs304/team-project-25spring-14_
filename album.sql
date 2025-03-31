-- 创建数据库
SET search_path TO public;
DROP DATABASE IF EXISTS smart_photo_album;
CREATE DATABASE smart_photo_album;

\c smart_photo_album;
DROP TYPE IF EXISTS user_status CASCADE;
DROP TYPE IF EXISTS privacy_type CASCADE;
DROP TYPE IF EXISTS resource_type CASCADE;
DROP TYPE IF EXISTS user_role CASCADE;
-- 创建类型
CREATE TYPE user_status AS ENUM ('active', 'disabled');
CREATE TYPE privacy_type AS ENUM ('private', 'public', 'shared');
CREATE TYPE resource_type AS ENUM ('album', 'photo');
CREATE TYPE user_role AS ENUM ('admin', 'user');
-- 注意删除表的顺序需要考虑外键依赖关系（先删除引用其他表的表）
DROP TABLE IF EXISTS tb_report CASCADE;
DROP TABLE IF EXISTS tb_admin_log CASCADE;
DROP TABLE IF EXISTS tb_ai_task CASCADE;
DROP TABLE IF EXISTS tb_photo_ai CASCADE;
DROP TABLE IF EXISTS tb_photo CASCADE;
DROP TABLE IF EXISTS tb_album CASCADE;
DROP TABLE IF EXISTS tb_user CASCADE;

DROP TABLE IF EXISTS tb_post CASCADE;
DROP TABLE IF EXISTS tb_comment CASCADE;
DROP TABLE IF EXISTS tb_like CASCADE;
DROP TABLE IF EXISTS tb_follow CASCADE ;
DROP TABLE IF EXISTS conversations CASCADE;
DROP TABLE IF EXISTS messages CASCADE ;

CREATE TABLE tb_user
(
    user_id      SERIAL PRIMARY KEY,
    rolename     user_role   DEFAULT 'user',
    username     VARCHAR(40)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    email        VARCHAR(60) UNIQUE,
    avatar_url   VARCHAR(255),
    status       user_status DEFAULT 'active',
    storage_used BIGINT      DEFAULT 0,
    created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    last_login   TIMESTAMP
);

CREATE TABLE tb_album
(
    album_id       SERIAL PRIMARY KEY,
    user_id        INTEGER      NOT NULL,
    title          VARCHAR(100) NOT NULL,
    description    TEXT,
    privacy        privacy_type DEFAULT 'private',
    cover_photo_id INTEGER,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id) ON DELETE CASCADE
);

CREATE INDEX idx_user_album ON tb_album (user_id);

CREATE TABLE tb_photo
(
    photo_id      SERIAL PRIMARY KEY,
    album_id      INTEGER      NOT NULL,
    user_id       INTEGER      NOT NULL,
    tag_name      VARCHAR(50),
    file_name     VARCHAR(255) NOT NULL,
    file_url      VARCHAR(255) NOT NULL,
    location VARCHAR(50) ,
    thumbnail_url VARCHAR(255) NOT NULL,
    is_favorite   BOOLEAN   DEFAULT FALSE,
    captured_at   TIMESTAMP, -- 拍摄时间
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (album_id) REFERENCES tb_album (album_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id) ON DELETE CASCADE
);

CREATE INDEX idx_album_photo ON tb_photo (album_id);
CREATE INDEX idx_user_photo ON tb_photo (user_id);
CREATE INDEX idx_captured_at ON tb_photo (captured_at);

CREATE TABLE tb_photo_ai
(
    photo_id     INTEGER PRIMARY KEY,
    objects      TEXT[],       -- 识别到的对象列表
    people       TEXT[],       -- 识别到的人物列表
    scene        VARCHAR(100), -- 场景类别
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (photo_id) REFERENCES tb_photo (photo_id) ON DELETE CASCADE
);

CREATE TABLE tb_ai_task
(
    task_id      SERIAL PRIMARY KEY,
    photo_id     INTEGER,
    task_type    VARCHAR(50) NOT NULL,          -- object_detection, face_recognition, etc
    status       VARCHAR(20) DEFAULT 'pending', -- pending, processing, completed, failed
    created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (photo_id) REFERENCES tb_photo (photo_id) ON DELETE SET NULL
);

CREATE INDEX idx_ai_task_status ON tb_ai_task (status);
CREATE INDEX idx_ai_task_photo ON tb_ai_task (photo_id);


CREATE TABLE tb_admin_log
(
    log_id      SERIAL PRIMARY KEY,
    admin_id    INTEGER     NOT NULL,
    action      VARCHAR(50) NOT NULL,
    target_type VARCHAR(50), -- user, photo, album
    target_id   INTEGER,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES tb_user (user_id) ON DELETE CASCADE
);

-- 内容举报表
CREATE TABLE tb_report
(
    report_id     SERIAL PRIMARY KEY,
    reporter_id   INTEGER       NOT NULL,
    resource_type resource_type NOT NULL,
    resource_id   INTEGER       NOT NULL,
    reason        VARCHAR(255)  NOT NULL,
    status        VARCHAR(20) DEFAULT 'pending', -- pending, reviewed, resolved
    reviewed_by   INTEGER,
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES tb_user (user_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES tb_user (user_id) ON DELETE SET NULL
);

CREATE INDEX idx_report_status ON tb_report (status);

CREATE TABLE tb_post
(
    post_id    SERIAL PRIMARY KEY,
    user_id    INTEGER NOT NULL,
    photo_id   INTEGER NOT NULL,
    caption    TEXT,
    privacy    privacy_type DEFAULT 'public',
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id) ON DELETE CASCADE,
    FOREIGN KEY (photo_id) REFERENCES tb_photo (photo_id) ON DELETE CASCADE
);

CREATE INDEX idx_user_post ON tb_post (user_id);
CREATE INDEX idx_photo_post ON tb_post (photo_id);

-- 评论表
CREATE TABLE tb_comment
(
    comment_id SERIAL PRIMARY KEY,
    post_id    INTEGER NOT NULL,
    user_id    INTEGER NOT NULL,
    content    TEXT    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES tb_post (post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id) ON DELETE CASCADE
);

CREATE INDEX idx_post_comment ON tb_comment (post_id);
CREATE INDEX idx_user_comment ON tb_comment (user_id);

CREATE TABLE tb_like
(
    like_id    SERIAL PRIMARY KEY,
    post_id    INTEGER NOT NULL,
    user_id    INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES tb_post (post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id) ON DELETE CASCADE,
    UNIQUE (post_id, user_id)
);

CREATE INDEX idx_post_like ON tb_like (post_id);
CREATE INDEX idx_user_like ON tb_like (user_id);


CREATE TABLE tb_follow (
  follow_id SERIAL PRIMARY KEY,
  follower_id INTEGER NOT NULL,
  following_id INTEGER NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (follower_id) REFERENCES tb_user(user_id) ON DELETE CASCADE,
  FOREIGN KEY (following_id) REFERENCES tb_user(user_id) ON DELETE CASCADE,
  UNIQUE (follower_id, following_id) -- 确保一个用户只能关注另一个用户一次
);

CREATE INDEX idx_follower ON tb_follow(follower_id);
CREATE INDEX idx_following ON tb_follow(following_id);


CREATE TABLE conversations
(
    conversation_id SERIAL PRIMARY KEY,
    user_id1        INT NOT NULL,
    user_id2        INT NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id1) REFERENCES tb_user (user_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id2) REFERENCES tb_user (user_id) ON DELETE CASCADE,
    CONSTRAINT unique_conversation UNIQUE (user_id1, user_id2) -- 确保每对用户只有一个对话
);
CREATE TABLE messages
(
    message_id      SERIAL PRIMARY KEY,
    conversation_id INT  NOT NULL,
    sender_id       INT  NOT NULL,
    content         TEXT NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations (conversation_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES tb_user (user_id) ON DELETE CASCADE
);

INSERT INTO tb_user (user_id,rolename,username,password,email,status,created_at)
VALUES (0,'admin'::user_role, 'virtual','123','admin@system.com', 'active'::user_status,CURRENT_TIMESTAMP);

INSERT INTO tb_album (album_id, user_id, title, description, privacy, created_at, updated_at)
VALUES (0, 0, '社区照片', '系统自动创建的社区照片专用相册，用户不可见', 'public'::privacy_type, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
