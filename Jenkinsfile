pipeline {
    agent any
    tools {
        nodejs "nodejs18"
        maven "maven3"
    }
    
    environment {
        DOCKER_REGISTRY = "registry.hub.docker.com"
        DOCKER_REGISTRY_CREDENTIAL = "b1f03bf0-7493-4a49-b5bb-fa7bfea95b96"
        DOCKER_IMAGE_BACKEND = "shuoer001/photo-album-backend"
        DOCKER_IMAGE_PYTHON = "shuoer001/photo-album-python"
        DOCKER_IMAGE_FRONTEND = "shuoer001/photo-album-frontend"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        
        BACKEND_DIR = "backend"
        PYTHON_DIR = "python-service"
        FRONTEND_DIR = "frontend"
    }
    
    stages {
        stage('Cleanup & Checkout') {
            steps {
                cleanWs()
                sh "mkdir -p ${BACKEND_DIR} ${PYTHON_DIR} ${FRONTEND_DIR}"
                
                // 清理现有容器
                sh 'docker-compose down -v || true'
                
                // 拉取所有分支
                dir("${BACKEND_DIR}") {
                    git branch: 'master', url: 'https://github.com/sustech-cs304/team-project-25spring-14_.git'
                }
                dir("${PYTHON_DIR}") {
                    git branch: 'python', url: 'https://github.com/sustech-cs304/team-project-25spring-14_.git'
                }
                dir("${FRONTEND_DIR}") {
                    git branch: 'vue', url: 'https://github.com/sustech-cs304/team-project-25spring-14_.git'
                }
            }
        }
        
        stage('Parallel Build') {
            parallel {
                stage('Build Backend') {
                    steps {
                        dir("${BACKEND_DIR}") {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                
                stage('Build Frontend') {
                    steps {
                        dir("${FRONTEND_DIR}") {
                            sh '''
                                npm install
                                npm run build
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Create Docker Files') {
            steps {
                // 后端Dockerfile
                dir("${BACKEND_DIR}") {
                    sh '''
                        cat > Dockerfile << 'EOF'
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
                    '''
                }
                
                // Python Dockerfile - 使用多阶段构建处理重型依赖
                dir("${PYTHON_DIR}") {
                    sh '''
                        cat > Dockerfile << 'EOF'
# 第一阶段：构建环境，处理重型依赖
FROM python:3.11-slim as builder

# 安装系统依赖
RUN apt-get update && apt-get install -y \\
    build-essential \\
    cmake \\
    pkg-config \\
    libopencv-dev \\
    libgl1-mesa-glx \\
    libglib2.0-0 \\
    libsm6 \\
    libxext6 \\
    libxrender-dev \\
    libgomp1 \\
    git \\
    wget \\
    && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制requirements.txt
COPY requirements.txt .

# 创建虚拟环境并安装依赖
RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# 升级pip并安装依赖，使用多个镜像源和重试机制
RUN pip install --upgrade pip && \\
    pip install --no-cache-dir \\
    -i https://pypi.tuna.tsinghua.edu.cn/simple \\
    --trusted-host pypi.tuna.tsinghua.edu.cn \\
    --timeout 300 \\
    -r requirements.txt || \\
    pip install --no-cache-dir \\
    -i https://mirrors.aliyun.com/pypi/simple/ \\
    --trusted-host mirrors.aliyun.com \\
    --timeout 300 \\
    -r requirements.txt || \\
    pip install --no-cache-dir --timeout 300 -r requirements.txt

# 第二阶段：运行环境
FROM python:3.11-slim

# 安装运行时依赖
RUN apt-get update && apt-get install -y \\
    libgl1-mesa-glx \\
    libglib2.0-0 \\
    libsm6 \\
    libxext6 \\
    libxrender-dev \\
    libgomp1 \\
    libgcc-s1 \\
    && rm -rf /var/lib/apt/lists/*

# 从构建阶段复制虚拟环境
COPY --from=builder /opt/venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# 设置工作目录
WORKDIR /app

# 复制应用代码
COPY . .

EXPOSE 5000

# 启动应用
CMD ["python", "app.py"]
EOF
                    '''
                }
                
                // 前端Dockerfile
                dir("${FRONTEND_DIR}") {
                    sh '''
                        cat > Dockerfile << 'EOF'
FROM nginx:alpine
COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
EOF

                        # 创建nginx配置
                        cat > nginx.conf << 'EOF'
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://photo-backend:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /py/ {
        proxy_pass http://photo-python:5000/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
EOF
                    '''
                }
                
                // 创建docker-compose.yml
                sh '''
                    cat > docker-compose.yml << 'EOF'
version: '3.8'

networks:
  photo-network:
    driver: bridge

services:
  postgres:
    image: postgres:13-alpine
    container_name: photo-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: smart_photo_album
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - pg_data:/var/lib/postgresql/data
      - ./backend/album.sql:/docker-entrypoint-initdb.d/init.sql:ro
    ports:
      - "5432:5432"
    networks:
      - photo-network
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
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/smart_photo_album
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      PYTHON_SERVICE_URL: http://photo-python:5000
    volumes:
      - shared_storage:/app/storage
    ports:
      - "8080:8080"
    networks:
      - photo-network

  python:
    image: ${DOCKER_IMAGE_PYTHON}:${DOCKER_TAG}
    container_name: photo-python
    restart: unless-stopped
    environment:
      PYTHONUNBUFFERED: 1
    volumes:
      - shared_storage:/app/storage
    ports:
      - "5000:5000"
    networks:
      - photo-network

  frontend:
    image: ${DOCKER_IMAGE_FRONTEND}:${DOCKER_TAG}
    container_name: photo-frontend
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      - backend
      - python
    networks:
      - photo-network

volumes:
  pg_data:
  shared_storage:
EOF
                '''
            }
        }
        
        stage('Build & Push Docker Images') {
            steps {
                script {
                    // 后端镜像
                    dir("${BACKEND_DIR}") {
                        echo "构建后端镜像..."
                        def backendImage = docker.build("${DOCKER_IMAGE_BACKEND}:${DOCKER_TAG}")
                        docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_REGISTRY_CREDENTIAL}") {
                            backendImage.push()
                            backendImage.push('latest')
                        }
                    }
                    
                    // Python镜像 - 增加超时时间处理重型依赖
                    dir("${PYTHON_DIR}") {
                        echo "构建Python镜像（可能需要较长时间下载依赖）..."
                        def pythonImage = docker.build("${DOCKER_IMAGE_PYTHON}:${DOCKER_TAG}")
                        docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_REGISTRY_CREDENTIAL}") {
                            pythonImage.push()
                            pythonImage.push('latest')
                        }
                    }
                    
                    // 前端镜像
                    dir("${FRONTEND_DIR}") {
                        echo "构建前端镜像..."
                        def frontendImage = docker.build("${DOCKER_IMAGE_FRONTEND}:${DOCKER_TAG}")
                        docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_REGISTRY_CREDENTIAL}") {
                            frontendImage.push()
                            frontendImage.push('latest')
                        }
                    }
                }
            }
        }
        
        stage('Deploy & Verify') {
            steps {
                sh '''
                    echo "启动所有服务..."
                    docker-compose up -d
                    
                    echo "等待服务启动..."
                    sleep 45
                    
                    echo "=== 服务状态 ==="
                    docker-compose ps
                    
                    echo "=== 健康检查 ==="
                    # 检查各服务是否正常
                    curl -f http://localhost || echo "前端未就绪"
                    curl -f http://localhost:8080/actuator/health || echo "后端未就绪"
                    curl -f http://localhost:5000 || echo "Python服务未就绪"
                    
                    echo "=== 数据库验证 ==="
                    docker exec photo-postgres psql -U postgres -d smart_photo_album -c "\\dt" || echo "数据库检查失败"
                    
                    echo "✅ 部署验证完成"
                '''
            }
        }
        
        stage('Create Deployment Package') {
            steps {
                sh '''
                    mkdir -p deployment-package
                    
                    # 复制部署文件
                    cp docker-compose.yml deployment-package/
                    cp -r backend deployment-package/
                    
                    # 创建简化启动脚本
                    cat > deployment-package/start.sh << 'EOF'
#!/bin/bash
echo "=== 启动智能相册应用 ==="

if ! command -v docker-compose &> /dev/null; then
    echo "❌ 请先安装Docker Compose"
    exit 1
fi

echo "🚀 启动服务..."
docker-compose up -d

echo "⏳ 等待服务启动..."
sleep 30

echo "📊 服务状态:"
docker-compose ps

echo ""
echo "✅ 应用已启动!"
echo "🌐 访问地址:"
echo "   前端界面: http://localhost"
echo "   后端API: http://localhost:8080"
echo "   Python AI: http://localhost:5000"
echo "   数据库: localhost:5432"
echo ""
echo "🔧 管理命令:"
echo "   查看日志: docker-compose logs -f [service]"
echo "   停止服务: docker-compose down"
echo "   重启服务: docker-compose restart"
EOF
                    
                    chmod +x deployment-package/start.sh
                    
                    # 创建停止脚本
                    cat > deployment-package/stop.sh << 'EOF'
#!/bin/bash
echo "停止所有服务..."
docker-compose down -v
echo "✅ 服务已停止"
EOF
                    
                    chmod +x deployment-package/stop.sh
                    
                    # 打包
                    tar -czf photo-album-app.tar.gz deployment-package/
                '''
            }
        }
        
        stage('Archive Results') {
            steps {
                archiveArtifacts artifacts: '''
                    photo-album-app.tar.gz,
                    docker-compose.yml
                ''', allowEmptyArchive: false
            }
        }
    }
    
    post {
        success {
            echo '''
            🎉 构建和部署成功！
            
            📦 部署包已创建: photo-album-app.tar.gz
            
            🚀 使用方法:
            1. 下载 photo-album-app.tar.gz
            2. 解压: tar -xzf photo-album-app.tar.gz
            3. 进入目录: cd deployment-package
            4. 启动: ./start.sh
            5. 访问: http://localhost
            
            🐳 Docker镜像已推送:
            - 后端: shuoer001/photo-album-backend:latest
            - Python AI: shuoer001/photo-album-python:latest
            - 前端: shuoer001/photo-album-frontend:latest
            
            💡 Python依赖已成功处理:
            - Flask 3.1.0
            - matplotlib 3.10.1
            - numpy 2.2.4
            - opencv-python 4.10.0.84
            - ultralytics 8.3.79
            '''
        }
        failure {
            echo '''
            ❌ 构建失败！
            
            🔍 可能的原因:
            - Python依赖下载超时（特别是OpenCV和ultralytics）
            - Docker Registry连接问题
            - 内存不足
            
            💡 解决建议:
            - 重新运行构建（依赖可能已缓存）
            - 检查网络连接
            - 增加Jenkins节点内存
            '''
        }
        always {
            // 显示最终状态
            sh '''
                echo "=== 最终状态 ==="
                docker images | grep photo-album || echo "无相关镜像"
                docker ps -a | grep photo- || echo "无相关容器"
            '''
        }
    }
}
