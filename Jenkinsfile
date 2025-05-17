pipeline {
    agent any
    tools {
        nodejs "nodejs18"
        maven "maven3"
    }
    
    stages {
        stage('Cleanup Workspace') {
            steps {
                // 清理工作空间
                cleanWs()
                sh "mkdir -p backend python-service frontend"
            }
        }
        
        stage('Checkout All Branches') {
            steps {
                echo "正在拉取所有分支代码..."
                
                // 拉取后端代码 (master分支)
                dir("backend") {
                    git branch: 'master', 
                        url: 'https://github.com/sustech-cs304/team-project-25spring-14_.git'
                }
                
                // 拉取Python服务代码
                dir("python-service") {
                    git branch: 'python', 
                        url: 'https://github.com/sustech-cs304/team-project-25spring-14_.git'
                }
                
                // 拉取前端代码
                dir("frontend") {
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
                        dir("backend") {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                
                stage('Setup Python') {
                    steps {
                        echo "处理Python服务..."
                        dir("python-service") {
                            // 检查Python依赖
                            sh '''
                                # 检查requirements.txt是否存在并记录
                                if [ -f requirements.txt ]; then
                                    echo "Found requirements.txt"
                                    cat requirements.txt > python-deps.txt
                                else
                                    echo "No requirements.txt found"
                                    echo "Flask" > python-deps.txt
                                    echo "numpy" >> python-deps.txt
                                    echo "opencv-python" >> python-deps.txt
                                fi
                                
                                # 查找主应用文件
                                if [ -f app.py ]; then
                                    echo "Found app.py" > python-info.txt
                                elif [ -f main.py ]; then
                                    echo "Found main.py" > python-info.txt
                                elif [ -f server.py ]; then
                                    echo "Found server.py" > python-info.txt
                                else
                                    echo "No main Python file found" > python-info.txt
                                    find . -name "*.py" | head -5 >> python-info.txt
                                fi
                            '''
                        }
                    }
                }
                
                stage('Build Frontend') {
                    steps {
                        echo "构建Vue前端..."
                        dir("frontend") {
                            sh 'npm install'
                            sh 'npm run build'
                        }
                    }
                }
            }
        }
        
        stage('Prepare Services') {
            steps {
                echo "准备服务..."
                
                dir("backend") {
                    // 查找JAR文件并记录
                    sh '''
                        find target -name "*.jar" | grep -v "sources" | grep -v "javadoc" > jar-files.txt
                        if [ -s jar-files.txt ]; then
                            echo "Found JAR files:"
                            cat jar-files.txt
                        else
                            echo "No JAR files found!"
                        fi
                    '''
                }
                
                dir("frontend") {
                    // 检查dist目录
                    sh '''
                        if [ -d "dist" ]; then
                            echo "Dist directory found" > frontend-info.txt
                            ls -la dist >> frontend-info.txt
                        else
                            echo "Dist directory not found!" > frontend-info.txt
                        fi
                    '''
                }
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                // 归档构建产物和信息文件
                dir("backend") {
                    archiveArtifacts artifacts: 'target/*.jar,jar-files.txt', allowEmptyArchive: true
                }
                
                dir("python-service") {
                    archiveArtifacts artifacts: 'python-deps.txt,python-info.txt,*.py', allowEmptyArchive: true
                }
                
                dir("frontend") {
                    archiveArtifacts artifacts: 'dist/**,frontend-info.txt', allowEmptyArchive: true
                }
            }
        }
        
        stage('Generate Deployment Scripts') {
            steps {
                echo "生成部署脚本..."
                
                // 创建启动脚本
                sh '''
                    # 创建启动脚本目录
                    mkdir -p deployment-scripts
                    
                    # 创建后端启动脚本
                    cat > deployment-scripts/start-backend.sh << 'EOF'
#!/bin/bash
# 后端启动脚本
cd backend
JAR_FILE=$(find target -name "*.jar" | grep -v "sources" | grep -v "javadoc" | head -1)
if [ -z "$JAR_FILE" ]; then
    echo "错误: 找不到JAR文件!"
    exit 1
fi
echo "启动后端服务: $JAR_FILE"
java -jar $JAR_FILE > backend.log 2>&1 &
echo $! > backend.pid
echo "后端服务已启动，PID: $(cat backend.pid)"
EOF
                    
                    # 创建Python服务启动脚本
                    cat > deployment-scripts/start-python.sh << 'EOF'
#!/bin/bash
# Python服务启动脚本
cd python-service
PY_FILE="app.py"
if [ ! -f "$PY_FILE" ]; then
    for f in main.py server.py index.py; do
        if [ -f "$f" ]; then
            PY_FILE="$f"
            break
        fi
    done
fi

if [ ! -f "$PY_FILE" ]; then
    echo "错误: 找不到Python主应用文件!"
    exit 1
fi

echo "启动Python服务: $PY_FILE"
python3 $PY_FILE > python.log 2>&1 &
echo $! > python.pid
echo "Python服务已启动，PID: $(cat python.pid)"
EOF
                    
                    # 创建前端启动脚本
                    cat > deployment-scripts/start-frontend.sh << 'EOF'
#!/bin/bash
# 前端服务启动脚本
cd frontend
if [ ! -d "dist" ]; then
    echo "错误: 找不到dist目录!"
    exit 1
fi

echo "启动前端服务..."
npx http-server dist -p 80 > frontend.log 2>&1 &
echo $! > frontend.pid
echo "前端服务已启动，PID: $(cat frontend.pid)"
EOF
                    
                    # 创建停止脚本
                    cat > deployment-scripts/stop-all.sh << 'EOF'
#!/bin/bash
# 停止所有服务
echo "停止所有服务..."

# 停止后端
if [ -f backend/backend.pid ]; then
    PID=$(cat backend/backend.pid)
    if ps -p $PID > /dev/null; then
        echo "停止后端服务 (PID: $PID)..."
        kill $PID
    else
        echo "后端服务不在运行"
    fi
    rm backend/backend.pid
fi

# 停止Python
if [ -f python-service/python.pid ]; then
    PID=$(cat python-service/python.pid)
    if ps -p $PID > /dev/null; then
        echo "停止Python服务 (PID: $PID)..."
        kill $PID
    else
        echo "Python服务不在运行"
    fi
    rm python-service/python.pid
fi

# 停止前端
if [ -f frontend/frontend.pid ]; then
    PID=$(cat frontend/frontend.pid)
    if ps -p $PID > /dev/null; then
        echo "停止前端服务 (PID: $PID)..."
        kill $PID
    else
        echo "前端服务不在运行"
    fi
    rm frontend/frontend.pid
fi

echo "所有服务已停止"
EOF

                    # 添加执行权限
                    chmod +x deployment-scripts/*.sh
                '''
                
                // 归档部署脚本
                archiveArtifacts artifacts: 'deployment-scripts/**', allowEmptyArchive: false
            }
        }
    }
    
    post {
        success {
            echo '''
            所有分支构建成功!
            
            您可以使用生成的部署脚本来启动和停止服务:
            - start-backend.sh: 启动后端服务
            - start-python.sh: 启动Python服务
            - start-frontend.sh: 启动前端服务
            - stop-all.sh: 停止所有服务
            
            这些脚本会保存在Jenkins工作区的deployment-scripts目录中，并已在构建产物中归档。
            '''
        }
        failure {
            echo '构建失败，请检查日志!'
        }
    }
}
