def call(){
    node('ssh-agent-node-01') {
        stage('Clone') {
            echo 'CLone'
        }
        stage('Build') {
            echo 'Build'
        }
    }
}
