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
                sshagent(['b1fd8109-9b99-4fd2-8db7-5a898625b64e']) {
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
