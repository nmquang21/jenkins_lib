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
            //choice(name: 'APP_BUILD', choices: ['Booking_UI', 'Booking_API'], description: 'Build app nào?')
            //multiSelect(name: 'APP_BUILD', choices: ['Booking_UI', 'Booking_API'], description: 'APP_BUILD')
            //string(name: 'APP_BUILD', defaultValue: 'Booking_UI,Booking_API', description: 'Build app nào?', allowMultiple: true)
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
            script{
                if (env.BRANCH_NAME == 'master') {
                    stage('Build 01') {
                        steps{
                            echo 'Build 01'
                        }

                    }
                } else {
                    stage('Build 02') {
                        steps{
                            echo 'Build 02'
                        }
                    }
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
