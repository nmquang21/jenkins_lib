def call(){
pipeline {
   // agent {
   //     node {
   //         label 'ssh-agent-node-01'
   //     }
   // }
    agent any
    stages {
       
        
        stage('Build') {
            steps {
                sh 'docker --version'
                sh 'docker ps'
                sshagent(['cfc80002-d34b-4b12-b640-fe12232c79f1']) {
                    // some block
                    sh 'ssh nmquang21@35.234.5.238 "touch /home/nmquang21/file_demo_ssh.txt"'
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
