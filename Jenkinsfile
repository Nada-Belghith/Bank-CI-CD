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
        
        // On définit notre nouvelle IP "magique"
        HOST_IP = "172.17.0.1"
    }

    stages {
        stage('Checkout') {
// ... existing code ...
        }

        stage('Build with Maven') {
// ... existing code ...
        }

        stage('Build Docker Image') {
// ... existing code ...
        }

        stage('Deploy Container') {
            steps {
                sh 'docker stop $APP_NAME || true'
                sh 'docker rm $APP_NAME || true'
                
                //
                // CORRECTION DB : On dit à Spring de trouver MySQL sur l'IP de l'hôte
                //
                sh '''
                   docker run -d -p 8083:8083 \
                   -e SPRING_DATASOURCE_URL="jdbc:mysql://${HOST_IP}:3306/mydb?useSSL=false" \
                   -e SPRING_DATASOURCE_USERNAME="root" \
                   -e SPRING_DATASOURCE_PASSWORD="" \
                   --name $APP_NAME $DOCKER_IMAGE
                '''
                
                //
                // CORRECTION ATTENTE : On utilise la nouvelle IP
                //
                sh '''
                    echo "Waiting for Spring Boot to start on http://${HOST_IP}:8083 ..."
                    ATTEMPTS=0
                    MAX=30
                    until curl -s -o /dev/null -w "%{http_code}" http://${HOST_IP}:8083/ | grep -E -q "[234].."; do
                        ATTEMPTS=$((ATTEMPTS+1))
                        if [ "$ATTEMPTS" -ge "$MAX" ]; then
                            echo "Timed out waiting for application to start"
                            echo "--- DOCKER LOGS ---"
                            docker logs $APP_NAME
                            echo "--- END DOCKER LOGS ---"
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
                //
                // CORRECTION JMETER : On dit à JMeter de tester la nouvelle IP
                //
                echo "Running post-deployment JMeter test against ${HOST_IP}:8083..."
                sh """
                    JMETER_JVM_ARGS="-Djava.net.preferIPv4Stack=true" ${env.JMETER_HOME}/bin/jmeter -n -t jmeter/performance_test_docker.jmx -l results_docker.jtl -Jhost=${HOST_IP} -Jport=8083
                """
            }
            post {
                always {
                    perfReport errorFailedThreshold: 5, sourceDataFiles: 'results_docker.jtl'
                }
            }
        }
    }


    post {
        // Le cleanup se fait à la fin, quoi qu'il arrive
        always {
            echo "Cleaning up Docker container..."
            sh 'docker stop $APP_NAME || true'
            sh 'docker rm $APP_NAME || true'
        }
    }
}