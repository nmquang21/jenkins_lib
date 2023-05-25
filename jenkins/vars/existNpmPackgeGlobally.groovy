def call(String packageName){
  def foobar = rumCmdStdout('npm list -g ${packageName}')
  return foobar.indexOf(packageName) != -1
}
