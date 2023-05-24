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
        stage('Example') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master') {
                        echo 'I only execute on the master branch'
                    } else {
                        echo 'I execute elsewhere'
                    }
                }
            }
        }
    }
}
