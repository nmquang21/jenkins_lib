def call(String commands){
  def returnStdout = ''
  try{
    if(isUnix()){
      returnStdout = sh(script: "${commands}", returnStdout: true)
    }
    else{
      returnStdout = bat(script: "${commands}", returnStdout: true)
    }
  }
  catch(ex){
    eturnStdout = ''
  }
  return eturnStdout
}
