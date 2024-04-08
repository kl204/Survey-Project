pipeline {
    agent any

    stages {

        stage('Check Port Usage') {
            steps {
                sshagent(credentials: ['SurveyProject']) {
                    script {
                        // 포트 8090을 사용하는 프로세스 종료 시도
                        sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com '
                        PID=$(sudo netstat -tulnp | grep ":8090" | awk '\''{print \$7}'\'' | cut -d"/" -f1)
                        if [ -n "$PID" ]; then
                            echo "Killing process $PID on port 8090."
                            sudo kill -SIGTERM $PID
                            sleep 10
                            if sudo kill -0 $PID 2>/dev/null; then
                                echo "Process $PID did not terminate, forcing kill."
                                sudo kill -9 $PID
                            fi
                        else
                            echo "No process running on port 8090."
                        fi
                        '

                        """
                    }
                }
            }
        }

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
                        // 포트 8090을 사용하는 프로세스 종료 시도
                        sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com '
                        PID=\$(sudo netstat -tulnp | grep ":8090" | awk "{print \$7}" | cut -d"/" -f1)
                        if [ -n "\$PID" ]; then
                            echo "Killing process \$PID on port 8090."
                            sudo kill -SIGTERM \$PID
                            sleep 10
                        else
                            echo "No process running on port 8090."
                        fi
                        '
                        """

                        // JAR 파일 전송
                        sh "scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/SurveyProject/target/survey-management-0.0.1-SNAPSHOT.jar ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com:/tmp"

                        // JAR 파일 이동
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com 'sudo mv /tmp/survey-management-0.0.1-SNAPSHOT.jar ./SurveyProject/'"

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
