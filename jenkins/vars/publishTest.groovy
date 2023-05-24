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
            choice(name: 'APP_BUILD', choices: ['Booking_UI', 'Booking_API'], description: 'Build app nào?')
        }
        environment {
            // Định nghĩa global
            BRANCH_NAME = 'masters'
        }
        stages {
            stage('Clone code') {
                steps{
                    echo 'Clone code'
                }               
            }
            stage('Build Image') {
                steps{
                    echo 'Build Image'
                } 
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
}
