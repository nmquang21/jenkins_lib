def call(){
    
    node('ssh-agent-node-01') {
        environment {
            // Định nghĩa global
            BRANCH_NAME = 'master'
        }
        stage('Clone') {
            echo env.BRANCH_NAME 
        }
        stage('Build') {
            echo 'Build'
        }
        stage('Example') {
            if (env.BRANCH_NAME == 'master') {
                echo 'I only execute on the master branch'
            } else {
                echo 'I execute elsewhere'
            }
        }
        if (env.BRANCH_NAME == 'master') {
            stage('Build 01') {
                echo 'Build 01'
            }
        } else {
            stage('Build 02') {
                echo 'Build 02'
            }
        }
    }
}
