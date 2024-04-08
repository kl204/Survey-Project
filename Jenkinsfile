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
                withCredentials([file(credentialsId: 'application_', variable: 'PROP_FILE')]) {
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
                        // 실행 중인 애플리케이션 종료
                        sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com '
                        pgrep -f survey-management-0.0.1-SNAPSHOT.jar | xargs -r sudo kill
                        '
                        """

                        // JAR 파일 전송
                        sh "scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/SurveyProject/target/survey-management-0.0.1-SNAPSHOT.jar ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com:/tmp"

                        // JAR 파일 이동
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com 'sudo mv /tmp/survey-management-0.0.1-SNAPSHOT.jar ./SurveyProject'"

                        // 애플리케이션 재시작
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com 'nohup sudo java -jar ./SurveyProject/survey-management-0.0.1-SNAPSHOT.jar > ./SurveyProject/log.txt 2>&1 &'"
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
