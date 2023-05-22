def call(){
  echo 'CALL BUILD IMAGES'
  sh 'npm install'
  sh 'npm run build'
}
