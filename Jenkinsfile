pipeline {
    agent any
    tools {
        nodejs "nodejs18"
        maven "maven3"
    }
    
    environment {
        // Docker相关配置
        DOCKER_REGISTRY = "registry.hub.docker.com"
        DOCKER_REGISTRY_CREDENTIAL = "b1f03bf0-7493-4a49-b5bb-fa7bfea95b96" // 使用您已有的凭证ID
        DOCKER_IMAGE_BACKEND = "shuoer001/photo-album-backend"
        DOCKER_IMAGE_FRONTEND = "shuoer001/photo-album-frontend"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        
        // 项目目录
        BACKEND_DIR = "backend"
        FRONTEND_DIR = "frontend"
        
        // 服务端口
        BACKEND_PORT = "8080"
        FRONTEND_PORT = "80"
        
        // 数据库配置
        DB_PORT = "5432"
        DB_NAME = "smart_photo_album"
        DB_USER = "postgres"
        DB_PASSWORD = "postgres"
    }
    
    stages {
        stage('Cleanup Workspace') {
            steps {
                cleanWs()
                sh "mkdir -p ${BACKEND_DIR} ${FRONTEND_DIR} sql-init"
                
                // 移除现有容器和卷，确保从头开始
                sh '''
                    # 停止并删除现有容器和卷
                    docker-compose down -v || true
                    
                    # 强制删除相关容器（如果存在）
                    docker rm -f photo-postgres photo-backend photo-frontend || true
                    
                    # 删除相关卷
                    docker volume rm $(docker volume ls -q | grep pg_data) || true
                    docker volume rm $(docker volume ls -q | grep shared_storage) || true
                '''
            }
        }
        
        stage('Checkout Branches') {
            steps {
                echo "正在拉取分支代码..."
                
                dir("${BACKEND_DIR}") {
                    git branch: 'master', 
                        url: 'https://github.com/sustech-cs304/team-project-25spring-14_.git'
                }
                
                dir("${FRONTEND_DIR}") {
                    git branch: 'vue', 
                        url: 'https://github.com/sustech-cs304/team-project-25spring-14_.git'
                }
            }
        }
        
        stage('Create Database Init Script') {
            steps {
                sh '''
                cat > sql-init/01-init-schema.sql << 'EOF'
-- PostgreSQL初始化脚本

-- 设置搜索路径
SET search_path TO public;

-- 创建类型
DROP TYPE IF EXISTS user_status CASCADE;
DROP TYPE IF EXISTS privacy_type CASCADE;
DROP TYPE IF EXISTS resource_type CASCADE;
DROP TYPE IF EXISTS user_role CASCADE;

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

CREATE TABLE tb_post
(
    post_id    SERIAL PRIMARY KEY,
    user_id    INTEGER NOT NULL,
    like_count INTEGER DEFAULT 0,
    caption    TEXT,
    privacy    privacy_type DEFAULT 'public',
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id) ON DELETE CASCADE
);

CREATE INDEX idx_user_post ON tb_post (user_id);

CREATE TABLE tb_photo
(
    photo_id      SERIAL PRIMARY KEY,
    album_id      INTEGER      NOT NULL,
    user_id       INTEGER      NOT NULL,
    tag_name      VARCHAR(50),
    file_name     VARCHAR(255) NOT NULL,
    file_url      VARCHAR(255) NOT NULL,
    location VARCHAR(50) ,
    thumbnail_url VARCHAR(255),
    is_favorite   BOOLEAN   DEFAULT FALSE,
    captured_at   TIMESTAMP, -- 拍摄时间
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    post_id      INTEGER,
    FOREIGN KEY (album_id) REFERENCES tb_album (album_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES tb_post (post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id) ON DELETE CASCADE
);
CREATE INDEX idx_photo_post ON tb_photo(post_id);
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
    resource_id   INTEGER       NOT NULL,       -- 被举报的资源的ID
    reportee_id   INTEGER       NOT NULL,       -- 被举报的用户的ID
    reason        VARCHAR(255)  NOT NULL,
    status        VARCHAR(20) DEFAULT 'pending', -- pending, reviewed, resolved
    reviewed_by   VARCHAR(40),
    is_corrected  BOOLEAN DEFAULT FALSE,
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES tb_user (user_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES tb_user (username) ON DELETE SET NULL
);

CREATE INDEX idx_report_status ON tb_report (status);


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
EOF

                # 确保SQL脚本有执行权限
                chmod 644 sql-init/*.sql
                ls -la sql-init/
                '''
            }
        }
        
        stage('Parallel Build') {
            parallel {
                stage('Build Backend') {
                    steps {
                        echo "构建Java后端..."
                        dir("${BACKEND_DIR}") {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                
                stage('Build Frontend') {
                    steps {
                        echo "构建Vue前端..."
                        dir("${FRONTEND_DIR}") {
                            sh 'npm install'
                            sh 'npm run build'
                        }
                    }
                }
            }
        }
        
        stage('Create Docker Files') {
            steps {
                echo "创建Docker文件..."
                
                // 后端Dockerfile - 使用Java 21
                dir("${BACKEND_DIR}") {
                    sh '''
                        cat > Dockerfile << 'EOF'
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
                    '''
                }
                
                // 前端Dockerfile - 移除所有Python服务引用
                dir("${FRONTEND_DIR}") {
                    sh '''
                        cat > Dockerfile << 'EOF'
FROM nginx:stable-alpine
COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
EOF

                        # 创建更简单的nginx配置 - 完全移除Python服务相关配置
                        cat > nginx.conf << 'EOF'
server {
    listen 80;
    server_name localhost;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # 只保留后端API代理
    location /api/ {
        proxy_pass http://photo-backend:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
EOF
                    '''
                }
                
                // 创建docker-compose.yml - 添加SQL初始化脚本挂载
                sh '''
                    cat > docker-compose.yml << 'EOF'
version: '3.8'

networks:
  photo-album-network:
    driver: bridge

services:
  postgres:
    image: postgres:13
    container_name: photo-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      # 确保PostgreSQL不使用数据卷缓存，每次都执行初始化脚本
      POSTGRES_INITDB_ARGS: "--no-sync"
    volumes:
      - pg_data:/var/lib/postgresql/data
      - ./sql-init:/docker-entrypoint-initdb.d
    ports:
      - "${DB_PORT}:5432"
    networks:
      - photo-album-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    image: ${DOCKER_IMAGE_BACKEND}:${DOCKER_TAG}
    container_name: photo-backend
    restart: unless-stopped
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
    volumes:
      - shared_storage:/app/storage
    ports:
      - "${BACKEND_PORT}:8080"
    networks:
      - photo-album-network

  frontend:
    image: ${DOCKER_IMAGE_FRONTEND}:${DOCKER_TAG}
    container_name: photo-frontend
    restart: unless-stopped
    ports:
      - "${FRONTEND_PORT}:80"
    depends_on:
      - backend
    networks:
      - photo-album-network

volumes:
  pg_data:
  shared_storage:
EOF
                '''

                // 创建简化版启动脚本
                sh '''
                    cat > deploy.sh << 'EOF'
#!/bin/bash

echo "=== 部署相册应用 ==="

# 停止所有现有容器并删除卷
echo "停止现有容器并删除卷..."
docker-compose down -v

# 启动所有服务
echo "启动服务..."
docker-compose up -d

# 等待服务启动
echo "等待服务启动..."
sleep 15

# 显示服务状态
echo "服务状态:"
docker-compose ps

# 检查数据库初始化状态
echo "检查数据库表结构:"
docker exec photo-postgres psql -U postgres -d smart_photo_album -c "\\dt"

# 检查前端日志确认没有错误
echo "前端容器日志:"
docker logs photo-frontend | grep -i error

echo "=== 部署完成 ==="
echo "访问地址: http://localhost"
EOF
                    chmod +x deploy.sh
                '''
            }
        }
        
        stage('Build & Push Docker Images') {
            steps {
                script {
                    // 后端镜像
                    dir("${BACKEND_DIR}") {
                        def backendImage = docker.build("${DOCKER_IMAGE_BACKEND}:${DOCKER_TAG}")
                        docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_REGISTRY_CREDENTIAL}") {
                            backendImage.push()
                            backendImage.push('latest')
                        }
                    }
                    
                    // 前端镜像
                    dir("${FRONTEND_DIR}") {
                        def frontendImage = docker.build("${DOCKER_IMAGE_FRONTEND}:${DOCKER_TAG}")
                        docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_REGISTRY_CREDENTIAL}") {
                            frontendImage.push()
                            frontendImage.push('latest')
                        }
                    }
                }
            }
        }
        
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // 使用改进的部署过程，确保清理和正确的初始化
                    sh '''
                        # 确保完全删除容器和卷
                        docker-compose down -v || true
                        
                        # 检查sql-init权限
                        ls -la sql-init/
                        
                        # 启动服务
                        docker-compose up -d
                        
                        # 等待服务启动
                        echo "等待PostgreSQL初始化完成..."
                        sleep 30
                        
                        # 检查PostgreSQL日志，确认初始化脚本是否执行
                        echo "PostgreSQL日志:"
                        docker logs photo-postgres | grep -i "init"
                        
                        # 验证容器状态
                        echo "服务状态:"
                        docker-compose ps
                        
                        # 如果数据库表未创建，手动执行SQL脚本
                        if ! docker exec photo-postgres psql -U ${DB_USER} -d ${DB_NAME} -c "\\dt" | grep -q "rows"; then
                            echo "表结构未创建，手动执行SQL脚本..."
                            cat sql-init/01-init-schema.sql | docker exec -i photo-postgres psql -U ${DB_USER} -d ${DB_NAME}
                        fi
                        
                        # 检查前端容器日志
                        echo "前端容器日志:"
                        docker logs photo-frontend
                    '''
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo "验证部署状态..."
                sh '''
                    # 确认所有服务正在运行
                    echo "检查容器状态..."
                    docker ps | grep photo-
                    
                    # 测试前端服务是否正常运行
                    if docker ps | grep -q photo-frontend; then
                        echo "✅ 前端容器运行正常"
                    else
                        echo "❌ 前端容器不在运行状态"
                        docker ps -a | grep photo-frontend
                        exit 1
                    fi
                    
                    # 测试后端服务是否正常运行
                    if docker ps | grep -q photo-backend; then
                        echo "✅ 后端容器运行正常"
                    else
                        echo "❌ 后端容器不在运行状态"
                        docker ps -a | grep photo-backend
                        exit 1
                    fi
                '''
            }
        }
        
        stage('Verify Database') {
            steps {
                sh '''
                    # 检查PostgreSQL日志
                    echo "PostgreSQL日志摘要:"
                    docker logs photo-postgres | grep -i "init\\|error\\|schema"
                    
                    # 检查表是否已创建
                    echo "检查数据库表结构..."
                    docker exec photo-postgres psql -U ${DB_USER} -d ${DB_NAME} -c "\\dt"
                    
                    # 验证初始数据是否已插入
                    echo "检查初始数据..."
                    docker exec photo-postgres psql -U ${DB_USER} -d ${DB_NAME} -c "SELECT * FROM tb_user WHERE username='virtual';"
                    
                    # 如果未能找到表，则手动执行一次
                    if ! docker exec photo-postgres psql -U ${DB_USER} -d ${DB_NAME} -c "\\dt" | grep -q "tb_user"; then
                        echo "尝试最后手动执行SQL..."
                        cat sql-init/01-init-schema.sql | docker exec -i photo-postgres psql -U ${DB_USER} -d ${DB_NAME}
                        
                        # 再次检查
                        echo "再次检查表结构..."
                        docker exec photo-postgres psql -U ${DB_USER} -d ${DB_NAME} -c "\\dt"
                        
                        if ! docker exec photo-postgres psql -U ${DB_USER} -d ${DB_NAME} -c "\\dt" | grep -q "tb_user"; then
                            echo "数据库表未能创建，检查是否存在问题!"
                            exit 1
                        fi
                    fi
                '''
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                // 归档构建产物
                dir("${BACKEND_DIR}") {
                    archiveArtifacts artifacts: 'target/*.jar,Dockerfile', allowEmptyArchive: true
                }
                
                dir("${FRONTEND_DIR}") {
                    archiveArtifacts artifacts: 'dist/**,Dockerfile,nginx.conf', allowEmptyArchive: true
                }
                
                // 归档Docker相关文件
                archiveArtifacts artifacts: 'docker-compose.yml,deploy.sh,sql-init/*', allowEmptyArchive: false
            }
        }
    }
    
    post {
        success {
            echo '''
            ✅ 构建与部署成功!
            
            🚀 应用已部署到Docker容器中：
            - 前端界面: http://localhost
            - 后端API: http://localhost:8080
            - PostgreSQL: localhost:5432
            
            📦 Docker镜像信息:
            - 后端镜像: shuoer001/photo-album-backend:latest (Java 21)
            - 前端镜像: shuoer001/photo-album-frontend:latest
            
            🔍 快速查看容器状态:
            - docker-compose ps
            - docker logs photo-frontend
            - docker logs photo-backend
            - docker exec photo-postgres psql -U postgres -d smart_photo_album -c "\\dt"
            
            💡 如需手动部署:
            - 下载docker-compose.yml和sql-init目录
            - 运行 docker-compose up -d
            '''
        }
        failure {
            echo '''
            ❌ 构建或部署失败!
            
            请检查以下日志和状态:
            - docker logs photo-frontend
            - docker logs photo-backend
            - docker logs photo-postgres
            - docker-compose ps
            
            数据库初始化检查:
            - docker exec photo-postgres psql -U postgres -d smart_photo_album -c "\\dt"
            '''
        }
    }
}
