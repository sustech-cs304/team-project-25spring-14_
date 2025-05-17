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
                sh "mkdir -p ${BACKEND_DIR} ${FRONTEND_DIR}"
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
                
                // 创建docker-compose.yml - 移除Python服务
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
    volumes:
      - pg_data:/var/lib/postgresql/data
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

# 停止所有现有容器
echo "停止现有容器..."
docker-compose down

# 启动所有服务
echo "启动服务..."
docker-compose up -d

# 等待服务启动
echo "等待服务启动..."
sleep 5

# 显示服务状态
echo "服务状态:"
docker-compose ps

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
                    // 使用简化的部署过程
                    sh '''
                        # 移除所有现有容器
                        docker-compose down || true
                        
                        # 启动服务
                        docker-compose up -d
                        
                        # 等待服务启动
                        echo "等待服务启动..."
                        sleep 20
                        
                        # 验证容器状态
                        echo "服务状态:"
                        docker-compose ps
                        
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
                archiveArtifacts artifacts: 'docker-compose.yml,deploy.sh', allowEmptyArchive: false
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
            
            💡 如需手动部署:
            - 下载docker-compose.yml文件
            - 运行 docker-compose up -d
            '''
        }
        failure {
            echo '''
            ❌ 构建或部署失败!
            
            请检查以下日志和状态:
            - docker logs photo-frontend
            - docker logs photo-backend
            - docker-compose ps
            '''
        }
    }
}
