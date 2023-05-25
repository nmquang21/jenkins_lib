import Contants

def call(){
    STRING_DELIMITER = ','
    BACKEND_GIT_URL = 'https://'
    FRONTEND_GIT_URL = 'https://'
    BACKEND_GIT_BRANCH = ''
    FRONTEND_GIT_BRANCH = ''

    BACKEND_WORKSPACE = ''
    FRONTEND_WORKSPACE = ''

    DOTNET_BASEIMAGE = 'aspnet:6.0.9'
    VUE_BASEIMAGE = 'nginx:1.23.1-alpine'

    PROJS = [:]
    PROJS['app/api'] = 'MISA.API'
    PROJS['app/ui'] = 'MISA.UI'

    pipeline{
        agent{
            node{
                label 'slave-node-01'
            }
        }
        options{
            disableConcurentBuilds()
            //buildDiscarder()
        }
        parameters{
            extendedChoice( 
                name: 'APP_BUILD', 
                defaultValue: 'app/ui,app/api', 
                mutiSelectDelimiter: STRING_DELIMITER, 
                quoteValue: false,
                saveJSONParameterToFile: false,
                type: 'PT_CHECKBOX', 
                description: 'Chon app build?', 
            )
            string(
                defaultValue: 'mimosa_git',
                name: 'CREDENTIALS_ID',
                trim: true,
                description: 'credetials de get code?',
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
                                getSourceTasks['Backend'] = {
                                    dir(BACKEND_WORKSPACE){
                                        script{
                                            checkout(
                                                [
                                                $class: 'GitSCM',
                                                branch: [[name: params.BACKEND_GIT_BRANCH]], 
                                                userRemoteConfigs:
                                                    [
                                                        [
                                                            credentialsId: params.CREDENTIALS_ID,
                                                            url: BACKEND_GIT_URL
                                                        ]
                                                    ] 
                                                ]
                                            )
                                        }
                                    }
                                }
                                getSourceTasks['FrontEnd'] = {
                                    dir(FRONTEND_WORKSPACE){
                                        script{
                                            checkout(
                                                [
                                                $class: 'GitSCM',
                                                branch: [[name: params.FRONTEND_GIT_BRANCH]], 
                                                userRemoteConfigs:
                                                    [
                                                        [
                                                            credentialsId: params.CREDENTIALS_ID,
                                                            url: FRONTEND_GIT_URL
                                                        ]
                                                    ] 
                                                ]
                                            )
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
                                taskPublish[app] = {
                                    node('slave-node-01'){
                                        def imageName = 'test'
                                        stage(app){
                                            FRONTEND_WORKSPACE = pwd()+ '/BOOKING'
                                            DEFAULT_FRONTEND_SOLUTION_DIR = "${FRONTEND_WORKSPACE}/misa.mimosa.ui"
                                            stage('get source'){
                                                dir(FRONTEND_WORKSPACE){
                                                    script{
                                                        checkout(
                                                            [
                                                            $class: 'GitSCM',
                                                            branch: [[name: params.FRONTEND_GIT_BRANCH]], 
                                                            userRemoteConfigs:
                                                                [
                                                                    [
                                                                        credentialsId: params.CREDENTIALS_ID,
                                                                        url: FRONTEND_GIT_URL
                                                                    ]
                                                                ] 
                                                            ]
                                                        )
                                                    }
                                                }
                                            }
                                            stage('npm build'){
                                                nodejs('NODEJS14'){
                                                    dir(DEFAULT_FRONTEND_SOLUTION_DIR){
                                                        if(!existNpmPackgeGlobally('@vue/cli')){
                                                            rumCmd('npm i -g @vue/cli')
                                                        }
                                                        def commands = [
                                                            'node -v',
                                                            'npm i',
                                                            'npm run build'
                                                        ]
                                                        commands.each{i ->
                                                            runCmd(i)
                                                        }
                                                    }
                                                }
                                            }
                                            stage('build image'){
                                                dir(DEFAULT_FRONTEND_SOLUTION_DIR){
                                                    def commands = [
                                                            // cmd docker    
                                                    ]
                                                    commands.each{i ->
                                                        runCmd(i)
                                                    }
                                                }
                                            }
                                            // ...
                                            stage('cleanup'){
                                                dir(DEFAULT_FRONTEND_SOLUTION_DIR){
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
                            }
                        }
                    }
                }
            }
        } 
    }
}