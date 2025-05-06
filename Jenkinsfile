pipeline {
    agent any
    tools {
        nodejs "nodejs18" // 替换为你在Jenkins中配置的NodeJS工具名称
    }
    stages {
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
        }
    }
}
