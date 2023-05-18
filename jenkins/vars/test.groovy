pipeline {
    agent {
        node {
            label 'ssh-agent-node-01'
        }
    }
    
    stages {
       
        
        stage('Build') {
            steps {
                sh 'docker --version'
                sh 'docker ps'
            }
        }
        
        stage('Test') {
            steps {
                echo 'mvn test'
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'mvn deploy'
            }
        }
    }
}






