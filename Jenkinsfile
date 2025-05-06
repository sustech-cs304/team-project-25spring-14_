pipeline {
    agent any
    tools {
        nodejs "nodejs18"
        maven "maven3"
    }
    stages {
        stage('Checkout') {
            steps {
                // 从GitHub克隆代码
                checkout scm
            }
        }
        stage('Build Spring Boot App') {
            steps {
                // 使用Maven构建应用
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Start Spring Boot App') {
            steps {
                // 启动应用前先查找jar文件
                script {
                    def jarFile = sh(script: 'find target -name "*.jar" | grep -v "sources" | grep -v "javadoc" | head -1', returnStdout: true).trim()
                    if (jarFile) {
                        echo "Found JAR file: ${jarFile}"
                        // 后台启动Spring Boot应用
                        sh "nohup java -jar ${jarFile} &"
                        // 等待应用启动
                        sh 'sleep 20'  // 增加等待时间确保启动完成
                        // 检查应用是否成功启动
                        sh 'curl -s http://localhost:8080/actuator/health || echo "Application health check failed"'
                    } else {
                        error "No JAR file found in target directory!"
                    }
                }
            }
        }
        stage('Install Apifox CLI') {
            steps {
                sh 'npm install -g apifox-cli'
            }
        }
        stage('Running Test Scenario') {
            steps {
                withCredentials([string(credentialsId: 'apifox-access-token', variable: 'APIFOX_ACCESS_TOKEN')]) {
                    sh 'apifox run --access-token $APIFOX_ACCESS_TOKEN -t 6481280 -e 29829339 -n 1 -r html,cli'
                }
            }
        }
        stage('Archive Test Reports') {
            steps {
                archiveArtifacts artifacts: 'apifox-reports/**', allowEmptyArchive: true
            }
        }
    }
    post {
        always {
            echo 'Test execution completed'
            // 停止所有Java进程（在Jenkins环境中比较安全，生产环境需要更精确的进程管理）
            sh 'pkill -f "java -jar" || true'
        }
    }
}
