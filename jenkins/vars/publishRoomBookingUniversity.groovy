def call(){
    pipeline {
        //agent any
        agent{
            node {
                       label 'ssh-agent-node-01'
             }
         }

        parameters {
            string(name: 'VERSION', defaultValue: 'latest', description: 'Phien ban build?')
        }
        environment {
            // Định nghĩa global
            BRANCH_NAME = 'masters'
        }
        stages {      
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
        post{
            always{
                sentMail('1.3')
            }
        }
    }
}
