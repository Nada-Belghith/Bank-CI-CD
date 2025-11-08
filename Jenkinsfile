pipeline {
    agent any

    tools {
        maven 'Maven'  // Configure "Maven" dans Jenkins > Global Tool Configuration
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
                git branch: 'main', url: 'https://Nada-Belghith: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@github.com/Nada-Belghith/Bank.git'
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
                    ${env.JMETER_HOME}/bin/jmeter -n -t jmeter/performance_test_local.jmx -l results_local.jtl -Jhost=localhost -Jport=8080
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
                                // wait for the app to be ready (poll /actuator/health or /)
                                sh '''
                                        echo "Waiting for Spring Boot to start on http://localhost:8081 ..."
                                        ATTEMPTS=0
                                        MAX=30
                                        until curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/ | grep -q "200"; do
                                            ATTEMPTS=$((ATTEMPTS+1))
                                            if [ "$ATTEMPTS" -ge "$MAX" ]; then
                                                echo "Timed out waiting for application to start"
                                                exit 1
                                            fi
                                            sleep 2
                                            echo "Waiting... ($ATTEMPTS)"
                                        done
                                        echo "Application is up"
                                '''
            }
        }

        stage('Post-Deployment Performance Test') {
            steps {
                echo "Running post-deployment JMeter test..."
                sh """
                    ${env.JMETER_HOME}/bin/jmeter -n -t jmeter/performance_test_docker.jmx -l results_docker.jtl -Jhost=localhost -Jport=8081
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
