//import Contants

def call(){
    STRING_DELIMITER = ','
    BACKEND_GIT_URL = 'https://github.com/PhamTam2k1/RoomBooking.git'
    FRONTEND_GIT_URL = 'https://github.com/PhamTam2k1/RoomBookingUniversityUI.git'

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
                description: 'Chon app build?', 
            )
            string(
                defaultValue: 'master',
                name: 'FRONTEND_GIT_BRANCH',
                trim: true,
                description: 'Build Front End nhánh nào?',
            )
            string(
                defaultValue: 'master',
                name: 'BACKEND_GIT_BRANCH',
                trim: true,
                description: 'Build Back End nhánh nào?',
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
                                        echo pwd()
                                        dir(BACKEND_WORKSPACE){
                                            script{
                                                checkout(
                                                    [
                                                    $class: 'GitSCM',
                                                    branch: [[name: params.FRONTEND_GIT_BRANCH]], 
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
                                        dir(FRONTEND_WORKSPACE){
                                            script{
                                                checkout(
                                                    [
                                                    $class: 'GitSCM',
                                                    branch: [[name: params.BACKEND_GIT_BRANCH]], 
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
                                    echo 'Puplish APP/FRONTEND'
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
