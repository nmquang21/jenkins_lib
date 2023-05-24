def call(){
    def globalEnv = [:] // Định nghĩa một đối tượng map để chứa các biến môi trường toàn cục
    environment {
        // Định nghĩa global
        globalEnv['BRANCH_NAME'] = 'master'
    }
    node('ssh-agent-node-01') {
        
        stage('Clone') {
            echo "Branch name: ${env.BRANCH_NAME}"
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
