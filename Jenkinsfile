pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-11.0.22.0.7-1.amzn2.0.1.x86_64'
    }

    stages {
        stage('Preparation') {
            steps {
                script {
                    if (sh(script: 'which mvn', returnStatus: true) != 0) {
                        echo 'Maven is not installed. Installing Maven...'
                        sh 'wget https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz -P /tmp'
                        sh 'tar xf /tmp/apache-maven-3.8.8-bin.tar.gz -C /opt'
                        sh 'ln -s /opt/apache-maven-3.8.8/bin/mvn /usr/bin/mvn'
                    }

                    if (!fileExists('.mvn/wrapper/maven-wrapper.jar') || !fileExists('mvnw')) {
                        sh 'mvn -N io.takari:maven:wrapper'
                        sh 'chmod +x ./mvnw'
                    }
                }
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./mvnw'
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
