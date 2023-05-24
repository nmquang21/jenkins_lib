def call(){
    node {
        stage('Clone') {
            echo 'CLone'
        }
        stage('Build') {
            echo 'Build'
        }
    }
}
