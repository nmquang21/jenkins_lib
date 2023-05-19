def call(){
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
                sshagent(credentials:['b1fd8109-9b99-4fd2-8db7-5a898625b64e']) {
                    // some block
                    sh 'ssh nmquang21@gmail.com@34.96.176.17 -o StrictHostKeyChecking=no "echo 1"'
                }
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
                echo 'mvn deployyy'
            }
        }
    }
}
}
