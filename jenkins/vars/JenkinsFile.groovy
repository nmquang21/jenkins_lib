def call(){
pipeline {
    agent {
        node {
            label 'ssh-agent-node-01'
        }
    }
    environment {
    /*
     define your command in variable
     */
    remoteCommands =
      """java --version;
    java --version;
    java --version """
  }
    stages {
       
        
        stage('Build') {
            steps {
                sh 'docker --version'
                sh 'docker ps'
                sshagent(credentials:['b1fd8109-9b99-4fd2-8db7-5a898625b64e']) {
                    // some bloc kmkdir 
                    sh 'ssh -o StrictHostKeyChecking=no -l root 34.96.176.17 docker pull nmquang21/jenkins_test:V1'
                    sh 'ssh -o StrictHostKeyChecking=no -l root 34.96.176.17 docker mkdir booking_hihi'
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
