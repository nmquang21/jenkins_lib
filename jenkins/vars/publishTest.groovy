def call(){
    environment {
        // Định nghĩa global
        BRANCH_NAME = 'masters'
    }
    node('ssh-agent-node-01') {
        stage('Clone') {
            echo 'CLone'
        }
        stage('Build') {
            echo 'Build'
        }
    }
}
