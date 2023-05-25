def call(String packageName){
  def foobar = runCmdStdout('npm list -g ${packageName}')
  return foobar.indexOf(packageName) != -1
}
