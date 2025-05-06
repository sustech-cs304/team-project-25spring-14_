pipeline {
  agent any

  tools {nodejs "{your_nodejs_configured_tool_name}"}

  stages {
    stage('Install Apifox CLI') {
      steps {
        sh 'npm install -g apifox-cli'
      }
    }

    stage('Running Test Scenario') {
      steps {
        sh 'apifox run --access-token APS-JELjYY5mE2KJVBIpvmO2Ez9zJvpvpMCT -t 6481280 -e 29829339 -n 1 -r html,cli'
      }
    }
  }
}
