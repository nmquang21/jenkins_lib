//import Contants

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
                                 getSourceTasks[app] = {
                                //     dir(BACKEND_WORKSPACE){
                                //         script{
                                //             checkout(
                                //                 [
                                //                 $class: 'GitSCM',
                                //                 branch: [[name: params.BACKEND_GIT_BRANCH]], 
                                //                 userRemoteConfigs:
                                //                     [
                                //                         [
                                //                             credentialsId: params.CREDENTIALS_ID,
                                //                             url: BACKEND_GIT_URL
                                //                         ]
                                //                     ] 
                                //                 ]
                                //             )
                                //         }
                                //     }
                                    echo app
                                 }
                                // getSourceTasks['FrontEnd'] = {
                                //     dir(FRONTEND_WORKSPACE){
                                //         script{
                                //             checkout(
                                //                 [
                                //                 $class: 'GitSCM',
                                //                 branch: [[name: params.FRONTEND_GIT_BRANCH]], 
                                //                 userRemoteConfigs:
                                //                     [
                                //                         [
                                //                             credentialsId: params.CREDENTIALS_ID,
                                //                             url: FRONTEND_GIT_URL
                                //                         ]
                                //                     ] 
                                //                 ]
                                //             )
                                //         }
                                //     }
                                // }
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
