def call(){
   BRANCH_NAME = 'master'
   BUILD_VERSION = '1.0.0'

    node('ssh-agent-node-01') {
        
        stage('Clone') {
            echo "Branch name: ${BRANCH_NAME}"
           stage('Parallel Stage') {
              cho "Parallel Stage"
          }
        }
        stage('Build') {
            echo 'Build'
        }
        stage('Example') {
            if (BRANCH_NAME == 'master') {
                echo 'I only execute on the master branch'
            } else {
                echo 'I execute elsewhere'
            }
        }
        if (BRANCH_NAME == 'master') {
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
