pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-11.0.22.0.7-1.amzn2.0.1.x86_64'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare application.properties') {
            steps {
                withCredentials([file(credentialsId: 'application', variable: 'PROP_FILE')]) {
                    sh 'cp $PROP_FILE src/main/resources/application.properties'
                }
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./mvnw'
                sh 'mvnw clean package -DskipTests'
            }
        }

        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                sshagent(credentials: ['Survey-pem']) {
                    script {
                        def remoteServer = 'ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com'
                        def targetPath = '/path/to/deploy/location/on/ec2'
                        def buildArtifact = 'target/your-spring-boot-app.jar'

                        sh "scp ${buildArtifact} ${remoteServer}:${targetPath}"

                        sh "ssh ${remoteServer} 'cd ${targetPath} && ./restart-spring-boot-app.sh'"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Build finished'
        }
        success {
            echo 'Build was a success!'
        }
        failure {
            echo 'Build failed.'
        }
    }
}
