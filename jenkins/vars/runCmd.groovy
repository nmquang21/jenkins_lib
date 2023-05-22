def call(String commands){
    echo 'RUN COMMANDS'
    def returnCode = 0
    try{
        if(isUnix()){
            returnCode = sh(script: "${commands}", returnStatus: true)
        }
        else{
            returnCode = bat(script: "${commands}", returnStatus: true)
        }
    }catch(ex){
        return code = -1
    }
    return returnCode == 0
}
