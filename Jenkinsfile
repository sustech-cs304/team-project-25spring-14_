pipeline {
    agent any
    environment {
        // 定义环境变量
        ACCESS_TOKEN = 'APS-JELjYY5mE2KJVBIpvmO2Ez9zJvpvpMCT'
        TEST_SUITE_ID = '6481280'
        ENVIRONMENT_ID = '29829339'
        SPRINGBOOT_PORT = '8080' // Spring Boot 应用程序的端口
    }

    stages {
        stage('Checkout Code') {
            steps {
                // 从 Git 仓库检出代码
                checkout scm
            }
        }

        stage('Install Dependencies') {
            steps {
                // 安装项目依赖
                sh 'npm install'
            }
        }

        stage('Build Spring Boot Application') {
            steps {
                // 构建 Spring Boot 应用程序
                sh './mvnw clean install'
            }
        }

        stage('Start Spring Boot Application') {
            steps {
                // 启动 Spring Boot 应用程序
                sh './mvnw spring-boot:run &'
                // 等待应用程序启动完成（根据需要调整等待时间）
                sh 'sleep 30'
            }
        }

        stage('Install Apifox CLI') {
            steps {
                // 安装 Apifox CLI
                sh 'npm install -g apifox-cli'
            }
        }

        stage('Running Test Scenario') {
            steps {
                // 运行测试场景
                sh "apifox run --access-token ${ACCESS_TOKEN} -t ${TEST_SUITE_ID} -e ${ENVIRONMENT_ID} -n 1 -r html,cli"
            }
        }

        stage('Stop Spring Boot Application') {
            steps {
                // 停止 Spring Boot 应用程序
                sh 'pkill -f spring-boot:run'
            }
        }
    }

    post {
        always {
            // 保存测试报告
            archiveArtifacts artifacts: 'apifox-report.html', fingerprint: true
        }
        cleanup {
            // 清理工作目录
            sh 'rm -rf *'
        }
    }
}
