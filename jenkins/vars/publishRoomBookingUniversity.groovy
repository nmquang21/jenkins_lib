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
            stage('Clone code') {
                steps {
                    git credentialsId: '1fd902f5-1ee3-4b89-b907-834346b62625', url: 'https://github.com/nmquang21/RoomBookingUniversityUI.git'
                }
            }
            stage('Push Image Docker hub') {
                steps {
                    withDockerRegistry(credentialsId: 'docker_hub', url: 'https://index.docker.io/v1/') {
                        sh 'docker build -t nmquang21/room_booking_university:${VERSION} .'
                        sh 'docker push nmquang21/room_booking_university:${VERSION}'
                        sh 'docker rmi nmquang21/room_booking_university:${VERSION}'
                    }
                }
            }
            stage('Build') {
                steps {
                    sh 'docker --version'
                    sh 'docker ps'
                    sshagent(credentials:['b1fd8109-9b99-4fd2-8db7-5a898625b64e']) {
                        // some bloc kmkdir 
                        sh 'ssh -o StrictHostKeyChecking=no -l root 34.96.176.17 docker pull nmquang21/room_booking_university:${VERSION}'
                        sh 'ssh -o StrictHostKeyChecking=no -l root 34.96.176.17 docker rm RoomBookingUniversity --force'
                        sh 'ssh -o StrictHostKeyChecking=no -l root 34.96.176.17 docker run -d --name RoomBookingUniversity -p 8880:80 nmquang21/room_booking_university:${VERSION}'
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
