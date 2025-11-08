pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    environment {
        APP_NAME = "Bank"
        DOCKER_IMAGE = "bank-app:latest"
        JMETER_HOME = "/opt/jmeter/apache-jmeter-5.6.3"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Nada-Belghith/Bank.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Deploy Container') {
            steps {
                sh "docker run -d -p 8081:8080 --name ${APP_NAME} ${DOCKER_IMAGE}"
            }
        }
    }

    // ✅ Post pipeline actions
    post {
        always {
            echo "Cleaning up Docker container..."
            sh "docker stop ${APP_NAME} || true"
            sh "docker rm ${APP_NAME} || true"
        }
    }
}
