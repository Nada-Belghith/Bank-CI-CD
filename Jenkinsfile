pipeline {
    agent any

    tools {
        maven 'Maven'  // Configure "Maven" dans Jenkins > Global Tool Configuration
    }

    environment {
        APP_NAME = "Bank"
        DOCKER_IMAGE = "Bank-app:latest"
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

        stage('Pre-Deployment Performance Test') {
            steps {
                echo "Running pre-deployment JMeter test..."
                sh """
                    ${env.JMETER_HOME}/bin/jmeter -n -t jmeter/performance_test_local.jmx -l results_local.jtl
                """
            }
            post {
                always {
                    perfReport errorFailedThreshold: 0, sourceDataFiles: 'results_local.jtl'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Deploy Container') {
            steps {
                sh 'docker run -d -p 8081:8080 --name $APP_NAME $DOCKER_IMAGE'
            }
        }

        stage('Post-Deployment Performance Test') {
            steps {
                echo "Running post-deployment JMeter test..."
                sh """
                    ${env.JMETER_HOME}/bin/jmeter -n -t jmeter/performance_test_docker.jmx -l results_docker.jtl
                """
            }
            post {
                always {
                    perfReport errorFailedThreshold: 0, sourceDataFiles: 'results_docker.jtl'
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up Docker container..."
            sh 'docker stop $APP_NAME || true'
            sh 'docker rm $APP_NAME || true'
        }
    }
}
