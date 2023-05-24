def call(){
   BRANCH_NAME = 'master'
   BUILD_VERSION = '1.0.0'
   def tasks = []

   tasks.add({
       // Tác vụ 1
       echo "Task 1 executed"
   })

   tasks.add({
       // Tác vụ 2
       echo "Task 2 executed"
   })
    node('ssh-agent-node-01') {
        stage('Parallel Stage') {
           parallel tasks
       }
        stage('Clone') {
            echo "Branch name: ${BRANCH_NAME}"
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
