@'
pipeline {
    agent any
    environment {
        IMAGE = "laminegl/emargement-professeurs:${env.BUILD_NUMBER}"
    }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Build Maven') { steps { sh 'mvn clean verify' } }
        stage('Docker Build') { steps { sh 'docker build -t $IMAGE .' } }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                  credentialsId: 'docker-hub-creds',
                  usernameVariable: 'DOCKER_USER',
                  passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                      echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                      docker push $IMAGE
                    '''
                }
            }
        }
    }
    post {
        success { echo "Build réussi : $IMAGE" }
        failure {
            mail to: 'vonlybetter3003@gmail.com,
                 subject: "Échec du build #${env.BUILD_NUMBER}",
                 body: "Consultez Jenkins pour les logs."
        }
    }
}
'@ | Set-Content -Path Jenkinsfile
