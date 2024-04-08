pipeline {
    agent any

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
                        // 특정 포트를 사용하는 애플리케이션의 프로세스 ID 찾기
                        def pid = sh(script: """
                            ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com '
                            sudo netstat -tulnp | grep ":8090" | awk '{print \$7}' | cut -d"/" -f1
                            '
                        """, returnStdout: true).trim()

                        // 애플리케이션 프로세스가 존재하면 종료
                        if (pid && pid.isDigit()) {
                            sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com 'sudo kill -SIGTERM ${pid}'"
                            // 프로세스 종료 확인
                            sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-44-207-93-37.compute-1.amazonaws.com 'while sudo kill -0 ${pid} 2> /dev/null; do sleep 1; done'"
                        }

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
