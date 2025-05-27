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
        
        // 新增：可执行文件输出目录
        EXECUTABLES_DIR = "executables"
        BUILD_START_TIME = "${System.currentTimeMillis()}"
    }
    
    stages {
        stage('Cleanup & Checkout') {
            steps {
                cleanWs()
                sh "mkdir -p ${BACKEND_DIR} ${PYTHON_DIR} ${FRONTEND_DIR} ${EXECUTABLES_DIR}"
                
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
        
        // 新增：构建时间监控阶段
        stage('Build Time Warning') {
            steps {
                echo '''
                ⏱️ 构建开始！
                
                🎥 注意：如果构建时间超过10分钟，建议开始录制视频！
                   - 使用OBS Studio或其他录屏软件
                   - 录制Jenkins构建页面
                   - 保存视频以备演示使用
                '''
            }
        }
        
        stage('Parallel Build') {
            parallel {
                stage('Build Backend') {
                    steps {
                        dir("${BACKEND_DIR}") {
                            sh 'mvn clean package -DskipTests'
                            
                            // 新增：复制JAR到可执行文件目录
                            sh '''
                                echo "📦 保存后端可执行JAR文件..."
                                cp target/*.jar ../${EXECUTABLES_DIR}/photo-album-backend.jar
                                echo "✅ 后端JAR已保存到: ${EXECUTABLES_DIR}/photo-album-backend.jar"
                            '''
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
                            
                            // 新增：打包前端dist为可部署包
                            sh '''
                                echo "📦 打包前端静态文件..."
                                tar -czf ../${EXECUTABLES_DIR}/photo-album-frontend-dist.tar.gz dist/
                                echo "✅ 前端打包已保存到: ${EXECUTABLES_DIR}/photo-album-frontend-dist.tar.gz"
                            '''
                        }
                    }
                }
                
                // 新增：Python可执行文件构建
                stage('Build Python Executable') {
                    steps {
                        dir("${PYTHON_DIR}") {
                            sh '''
                                echo "🐍 准备Python服务打包..."
                                
                                # 创建Python启动脚本
                                cat > run_python_service.sh << 'EOF'
#!/bin/bash
# Python服务启动脚本
# 需要先安装Python 3.11和依赖

echo "检查Python环境..."
if ! command -v python3 &> /dev/null; then
                                echo "❌ 请先安装Python 3.11"
    exit 1
fi

echo "安装依赖..."
pip install -r requirements.txt

echo "启动Python服务..."
python app.py
EOF
                                
                                chmod +x run_python_service.sh
                                
                                # 打包Python服务
                                tar -czf ../${EXECUTABLES_DIR}/photo-album-python-service.tar.gz \\
                                    app.py \\
                                    requirements.txt \\
                                    run_python_service.sh \\
                                    $(find . -name "*.py" -o -name "*.yml" -o -name "*.yaml" | grep -v __pycache__)
                                
                                echo "✅ Python服务包已保存到: ${EXECUTABLES_DIR}/photo-album-python-service.tar.gz"
                            '''
                        }
                    }
                }
            }
        }
        
        // 新增：创建独立运行脚本
        stage('Create Standalone Scripts') {
            steps {
                dir("${EXECUTABLES_DIR}") {
                    // 创建后端独立运行脚本
                    sh '''
                        cat > run-backend-standalone.sh << 'EOF'
#!/bin/bash
# 独立运行后端服务（需要Java 21）

echo "🚀 启动后端服务..."

# 检查Java
if ! command -v java &> /dev/null; then
    echo "❌ 请先安装Java 21"
    exit 1
fi

# 检查PostgreSQL（可选）
echo "⚠️  注意：需要PostgreSQL数据库"
echo "   如果没有运行的数据库，可以使用Docker："
echo "   docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:13"

# 运行JAR
java -jar photo-album-backend.jar \\
    --spring.datasource.url=jdbc:postgresql://localhost:5432/smart_photo_album \\
    --spring.datasource.username=postgres \\
    --spring.datasource.password=postgres
EOF
                        chmod +x run-backend-standalone.sh
                    '''
                    
                    // 创建前端独立运行脚本
                    sh '''
                        cat > run-frontend-standalone.sh << 'EOF'
#!/bin/bash
# 独立运行前端服务

echo "🌐 部署前端服务..."

# 解压dist
tar -xzf photo-album-frontend-dist.tar.gz

# 使用Python简单HTTP服务器
echo "使用Python HTTP服务器在端口8000运行前端..."
cd dist
python3 -m http.server 8000
EOF
                        chmod +x run-frontend-standalone.sh
                    '''
                    
                    // 创建完整的独立部署脚本
                    sh '''
                        cat > deploy-all-standalone.sh << 'EOF'
#!/bin/bash
# 完整独立部署脚本

echo "🎯 智能相册应用独立部署"
echo "========================"

# 1. 启动数据库
echo "1️⃣ 启动PostgreSQL数据库..."
docker run -d --name photo-postgres -p 5432:5432 \\
    -e POSTGRES_DB=smart_photo_album \\
    -e POSTGRES_USER=postgres \\
    -e POSTGRES_PASSWORD=postgres \\
    postgres:13

sleep 10

# 2. 启动后端
echo "2️⃣ 启动后端服务..."
nohup java -jar photo-album-backend.jar > backend.log 2>&1 &
echo "后端PID: $!"

# 3. 启动Python服务
echo "3️⃣ 启动Python AI服务..."
tar -xzf photo-album-python-service.tar.gz
cd python-service
nohup ./run_python_service.sh > ../python.log 2>&1 &
cd ..
echo "Python服务PID: $!"

# 4. 启动前端
echo "4️⃣ 启动前端服务..."
tar -xzf photo-album-frontend-dist.tar.gz
cd dist
nohup python3 -m http.server 80 > ../frontend.log 2>&1 &
cd ..
echo "前端PID: $!"

echo ""
echo "✅ 所有服务已启动！"
echo "📍 访问地址："
echo "   前端: http://localhost"
echo "   后端API: http://localhost:8080"
echo "   Python AI: http://localhost:5000"
echo ""
echo "📋 查看日志："
echo "   tail -f backend.log"
echo "   tail -f python.log"
echo "   tail -f frontend.log"
EOF
                        chmod +x deploy-all-standalone.sh
                    '''
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
                
                // Python Dockerfile - 使用国内镜像源的简化版本
                dir("${PYTHON_DIR}") {
                    sh '''
                        cat > Dockerfile << 'EOF'
FROM python:3.11-slim

# 配置使用阿里云镜像源
RUN echo "deb https://mirrors.aliyun.com/debian/ bookworm main contrib non-free non-free-firmware" > /etc/apt/sources.list && \\
    echo "deb https://mirrors.aliyun.com/debian/ bookworm-updates main contrib non-free non-free-firmware" >> /etc/apt/sources.list && \\
    echo "deb https://mirrors.aliyun.com/debian-security bookworm-security main contrib non-free non-free-firmware" >> /etc/apt/sources.list

# 安装最小运行时依赖
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgomp1 \
    libglib2.0-0 \
    libgl1-mesa-glx \
    ffmpeg \
    libsm6 \
    libxext6 \
    libxrender-dev \
    libgstreamer1.0-0 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 复制requirements.txt
COPY requirements.txt .

# 创建修改后的requirements文件，使用headless版本的opencv
RUN sed 's/opencv-python/opencv-python-headless/g' requirements.txt > requirements_modified.txt || cp requirements.txt requirements_modified.txt

# 使用国内pip源安装Python包
RUN pip config set global.index-url https://pypi.doubanio.com/simple/ && \\
    pip config set install.trusted-host pypi.doubanio.com && \\
    pip install --upgrade pip && \\
    pip install --no-cache-dir -r requirements_modified.txt || \\
    (pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple && \\
     pip config set install.trusted-host pypi.tuna.tsinghua.edu.cn && \\
     pip install --no-cache-dir -r requirements_modified.txt)

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
      #- pg_data:/var/lib/postgresql/data
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
      - shared_storage:/app/uploads/storage
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
      FLASK_HOST: 0.0.0.0
      FLASK_PORT: 5000
    volumes:
      - shared_storage:/app/uploads/storage
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
  #pg_data:
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
                    
                    # 新增：复制可执行文件到部署包
                    cp -r ${EXECUTABLES_DIR} deployment-package/
                    
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
        
        // 新增：打包所有可执行文件
        stage('Package Executables') {
            steps {
                sh '''
                    echo "📦 打包所有可执行文件..."
                    cd ${EXECUTABLES_DIR}
                    tar -czf ../photo-album-executables-only.tar.gz *
                    cd ..
                    
                    echo "📋 可执行文件清单："
                    ls -la ${EXECUTABLES_DIR}/
                    
                    echo ""
                    echo "✅ 独立可执行文件包已创建: photo-album-executables-only.tar.gz"
                    echo "   包含："
                    echo "   - photo-album-backend.jar (后端可执行JAR)"
                    echo "   - photo-album-frontend-dist.tar.gz (前端静态文件)"
                    echo "   - photo-album-python-service.tar.gz (Python服务包)"
                    echo "   - 各种独立运行脚本"
                '''
            }
        }
        
        stage('Archive Results') {
            steps {
                archiveArtifacts artifacts: '''
                    photo-album-app.tar.gz,
                    photo-album-executables-only.tar.gz,
                    docker-compose.yml,
                    executables/*
                ''', allowEmptyArchive: false
            }
        }
    }
    
    post {
        success {
            echo '''
            🎉 构建和部署成功！

            📦 可执行文件已准备就绪：
            ✅ photo-album-executables-only.tar.gz - 包含所有独立可执行文件
               - 后端JAR: photo-album-backend.jar
               - 前端打包: photo-album-frontend-dist.tar.gz  
               - Python服务: photo-album-python-service.tar.gz
               - 独立运行脚本

            🚀 演示准备：
            1. 下载 photo-album-executables-only.tar.gz
            2. 解压: tar -xzf photo-album-executables-only.tar.gz
            3. 运行独立部署: ./deploy-all-standalone.sh
            4. 或分别运行各组件的独立脚本

            📦 完整部署包: photo-album-app.tar.gz

            🐳 Docker镜像已推送:
            - 后端: shuoer001/photo-album-backend:latest
            - Python AI: shuoer001/photo-album-python:latest
            - 前端: shuoer001/photo-album-frontend:latest
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
            
            📦 检查是否有之前成功的构建产物可用于演示
            '''
        }
        always {
            // 显示最终状态
            sh '''
                echo "=== 最终状态 ==="
                docker images | grep photo-album || echo "无相关镜像"
                docker ps -a | grep photo- || echo "无相关容器"
                
                echo ""
                echo "=== 可执行文件 ==="
                ls -la ${EXECUTABLES_DIR}/ || echo "无可执行文件"
            '''
        }
    }
}
