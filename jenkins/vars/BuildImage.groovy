def call(){
  echo 'CALL BUILD IMAGES'
  def commands = [
    'npm install',
    'npm run build'
  ]
  commands.each{ i ->
    runCmd(i)
  }
  //sh 'npm install'
  //sh 'npm run build'
}
