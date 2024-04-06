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

        // Maven Wrapper 생성 단계 추가
        stage('Setup Maven Wrapper') {
            steps {
                // Maven이 이미 설치되어 있고, mvn 명령어를 사용할 수 있는 경우
                script {
                    // .mvn 디렉토리 또는 mvnw 파일이 존재하지 않을 때만 Maven Wrapper 생성
                    if (!fileExists('.mvn/wrapper/maven-wrapper.jar') || !fileExists('mvnw')) {
                        sh 'mvn -N io.takari:maven:wrapper'
                        // Linux 환경에서 실행 권한 부여
                        sh 'chmod +x mvnw'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean package'
            }
        }

        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                sshagent(credentials: ['ssh-key-pairs']) {
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
