//import Contants

def call(){
    STRING_DELIMITER = ','
    BACKEND_GIT_URL = 'https://github.com/PhamTam2k1/RoomBooking.git'
    FRONTEND_GIT_URL = 'https://github.com/PhamTam2k1/RoomBookingUniversityUI.git'
    VERSION = 'latest'
    BACKEND_WORKSPACE = 'BACKEND'
    FRONTEND_WORKSPACE = 'FRONTEND'

    DOTNET_BASEIMAGE = 'aspnet:6.0.9'
    VUE_BASEIMAGE = 'nginx:1.23.1-alpine'

    PROJS = [:]
    PROJS['app/api'] = 'MISA.API'
    PROJS['app/ui'] = 'MISA.UI'

    pipeline{
        agent{
            node{
                label 'ssh-agent-node-01'
            }
        }
        //options{
        //  disableConcurentBuilds()
            //buildDiscarder()
        // }
        parameters{
            extendedChoice( 
                name: 'APP_BUILD', 
                value: 'APP/FRONTEND,APP/BACKEND', 
                multiSelectDelimiter: STRING_DELIMITER, 
                quoteValue: false,
                saveJSONParameterToFile: false,
                type: 'PT_CHECKBOX', 
                description: 'Chọn App muốn build?', 
            )
            string(
                defaultValue: 'master',
                name: 'FRONTEND_GIT_BRANCH',
                trim: true,
                description: 'Build FrontEnd nhánh nào?',
            )
            string(
                defaultValue: 'master',
                name: 'BACKEND_GIT_BRANCH',
                trim: true,
                description: 'Build BackEnd nhánh nào?',
            )
        }
        stages{
            stage('get lastet source'){
                steps{
                    script{
                        getSourceTasks = [:]
                        selectedAppBuild = params.APP_BUILD.split(STRING_DELIMITER)
                        if(params.APP_BUILD != '' && params.APP_BUILD != STRING_DELIMITER && selectedAppBuild.size() > 0){
                            selectedAppBuild.each{app ->
                                if(app == 'APP/FRONTEND'){
                                    getSourceTasks[app] = {
                                        dir(FRONTEND_WORKSPACE){
                                            script{
                                                checkout(
                                                    [
                                                    $class: 'GitSCM',
                                                    branch: params.FRONTEND_GIT_BRANCH, 
                                                    userRemoteConfigs:
                                                        [
                                                            [
                                                                credentialsId: '1fd902f5-1ee3-4b89-b907-834346b62625',
                                                                url: FRONTEND_GIT_URL
                                                            ]
                                                        ] 
                                                    ]
                                                )
                                            }
                                        }
                                    }
                                }
                                if(app == 'APP/BACKEND'){
                                    getSourceTasks['FrontEnd'] = {
                                        echo pwd()
                                        dir(BACKEND_WORKSPACE){
                                            script{
                                                checkout(
                                                    [
                                                    $class: 'GitSCM',
                                                    branch: params.BACKEND_GIT_BRANCH, 
                                                    userRemoteConfigs:
                                                        [
                                                            [
                                                                credentialsId: '1fd902f5-1ee3-4b89-b907-834346b62625',
                                                                url: BACKEND_GIT_URL
                                                            ]
                                                        ] 
                                                    ]
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        parallel getSourceTasks
                    }
                }
            }
            stage('publish'){
                steps{
                    script{
                        taskPublish = [:]
                        selectedAppBuild = params.APP_BUILD.split(STRING_DELIMITER)
                        if(params.APP_BUILD != '' && params.APP_BUILD != STRING_DELIMITER && selectedAppBuild.size() > 0){
                            selectedAppBuild.each{app ->
                                if(app == 'APP/FRONTEND'){
                                    taskPublish[app] = {
                                        stage(app){
                                            stage('npm build'){
                                                dir(FRONTEND_WORKSPACE){
                                                    if(!existNpmPackgeGlobally('@vue/cli')){
                                                        //runCmd('npm i -g @vue/cli')
                                                    }
                                                    def commands = [
                                                        'node -v',
                                                        'npm i',
                                                        'npm run build'
                                                    ]
                                                    commands.each{i ->
                                                        //runCmd(i)
                                                    }
                                                }
                                            }
                                            stage('build image'){
                                                dir(FRONTEND_WORKSPACE){
                                                    echo pwd()
                                                    echo VERSION
                                                    echo 'docker build -t nmquang21/room_booking_university:latest .'
                                                    runCmd('docker build -t nmquang21/room_booking_university:latest .')
                                            
                                                }
                                            }
                                            stage('push image to DockerHub') {
                                                dir(FRONTEND_WORKSPACE){
                                                    steps {
                                                        withDockerRegistry(credentialsId: 'docker_hub', url: 'https://index.docker.io/v1/') {
                                                            runCmd('docker push nmquang21/room_booking_university:latest')
                                                        }
                                                        runCmd('docker rmi nmquang21/room_booking_university:latest')
                                                    }
                                                }
                                            }
                                            stage('Build FRONTEND') {
                                                dir(FRONTEND_WORKSPACE){
                                                    steps {
                                                        sshagent(credentials:['b1fd8109-9b99-4fd2-8db7-5a898625b64e']) {
                                                            def commands = [
                                                                'ssh -o StrictHostKeyChecking=no -l root 34.96.176.17 docker pull nmquang21/room_booking_university:latest',
                                                                'ssh -o StrictHostKeyChecking=no -l root 34.96.176.17 docker rm RoomBookingUniversity --force',
                                                                'ssh -o StrictHostKeyChecking=no -l root 34.96.176.17 docker run -d --name RoomBookingUniversity -p 80:80 nmquang21/room_booking_university:latest'
                                                            ]
                                                            commands.each{i ->
                                                                runCmd(i)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            stage('cleanup'){
                                                echo 'cleanup'
                                                dir(FRONTEND_WORKSPACE){
                                                    if(isUnix()){
                                                        sh 'rm -rf ./dist'
                                                    }else{
                                                        bat '''rd /s /q "./dist"'''
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if(app == 'APP/BACKEND'){
                                    echo 'Puplish APP/BACKEND'
                                }
                            }
                            parallel taskPublish
                        }
                    }
                }
            }
        } 
    }
}
