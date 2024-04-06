pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-11.0.22.0.7-1.amzn2.0.1.x86_64'
        REMOTE_SERVER = 'ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com'
        TARGET_PATH = '/SurveyProject'
        BUILD_ARTIFACT = 'target/survey-management-0.0.1-SNAPSHOT.jar'
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
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                sshagent(credentials: ['SurveyProject']) {
                    script {
                        sh "scp -o StrictHostKeyChecking=no target/survey-management-0.0.1-SNAPSHOT.jar ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com:/tmp"
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com 'sudo mv /tmp/survey-management-0.0.1-SNAPSHOT.jar /SurveyProject'"
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com 'sudo pkill -f survey-management'"
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com 'nohup sudo java -jar /SurveyProject/survey-management-0.0.1-SNAPSHOT.jar > /SurveyProject/log.txt 2>&1 &'"
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
