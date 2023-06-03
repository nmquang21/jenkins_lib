//import Contants

def call(){
    STRING_DELIMITER = ','
    BACKEND_GIT_URL = 'https://github.com/vucongdoan306/CinemaTheater.git'
    FRONTEND_GIT_URL =  'https://github.com/vucongdoan306/vite-soft-ui-dashboard.git'
    VERSION = 'latest'
    BACKEND_WORKSPACE = 'VCDOAN/BACKEND'
    FRONTEND_WORKSPACE = 'VCDOAN/FRONTEND'
    PROJS = [:]
    PROJS['app/api'] = 'MISA.API'
    PROJS['app/ui'] = 'MISA.UI'

    pipeline{
            agent any
        //agent{
           // any
         //   node{
               // label 'ssh-agent-node-01'
           // }
        //}
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
                                            echo 'VCDOAN'
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
                                                        'whoami'
                                                        'node -v',
                                                        'npm i',
                                                        'npm run build'
                                                    ]
                                                    commands.each{i ->
                                                        runCmd(i)
                                                    }
                                                }
                                            }
                                            stage('build  frontend'){
                                                dir(FRONTEND_WORKSPACE){
                                                        def commands = [
                                                            'docker build -t nmquang21/cinema_ui_das:latest .',
                                                            'docker rm CinemaUIDas --force',
                                                            'docker run -d --name CinemaUIDas -p 80:88 nmquang21/cinema_ui_das:latest',
                                                            'docker rmi nmquang21/cinema_ui_das:latest'
                                                        ]
                                                        commands.each{i ->
                                                            runCmd(i)
                                                        }
                                                }
                                            }
                                            
                                            stage('cleanup frontend'){
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
                                   taskPublish[app] = {
                                        stage(app){
                                            stage('build image backend'){
                                                dir(BACKEND_WORKSPACE){
                                                    echo pwd()
                                                    runCmd()
                                                        def commands = [
                                                            'docker build -t nmquang21/cinema_api:latest .',
                                                            'docker rm CinemaAPI --force',
                                                            'docker run -d --name CinemaAPI --network=mysql_my_network_vcdoan -p 8889:80 nmquang21/cinema_api:latest',
                                                            'docker rmi nmquang21/cinema_api:latest'
                                                        ]
                                                        commands.each{i ->
                                                            runCmd(i)
                                                        }
                                                }
                                            }
                                        }
                                    }
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
