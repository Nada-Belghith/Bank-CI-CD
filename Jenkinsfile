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
                git branch: 'main', url: 'https://Nada-Belghith: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@github.com/Nada-Belghith/Bank.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Deploy Container') {
            steps {
                // Nettoyage avant de lancer
                sh 'docker stop $APP_NAME || true'
                sh 'docker rm $APP_NAME || true'
                
                // Démarrage du conteneur avec les variables de BDD
                // (host.docker.internal est CORRECT ici, car c'est DEPUIS le conteneur)
                sh '''
                   docker run -d -p 8083:8083 \
                   -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/mydb?useSSL=false" \
                   -e SPRING_DATASOURCE_USERNAME="root" \
                   -e SPRING_DATASOURCE_PASSWORD="" \
                   --name $APP_NAME $DOCKER_IMAGE
                '''
                
                // Script d'attente
                // (127.0.0.1 est CORRECT ici, car c'est DEPUIS l'hôte)
                sh '''
                    echo "Waiting for Spring Boot to start on http://127.0.0.1:8083 ..."
                    ATTEMPTS=0
                    MAX=30
                    until curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8083/ | grep -E -q "[234].."; do
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
                #
                # CORRECTION FINALE : JMeter (sur l'hôte) doit tester 127.0.0.1
                #
                echo "Running post-deployment JMeter test against 127.0.0.1:8083..."
                sh """
                    # On garde le fix IPv4 pour Java
                    JMETER_JVM_ARGS="-Djava.net.preferIPv4Stack=true" ${env.JMETER_HOME}/bin/jmeter -n -t jmeter/performance_test_docker.jmx -l results_docker.jtl -Jhost=127.0.0.1 -Jport=8083
                """
            }
            post {
                always {
                    perfReport errorFailedThreshold: 5, sourceDataFiles: 'results_docker.jtl'
                }
            }
        }
    } // Fin du bloc 'stages'

    post {
        always {
            echo "Cleaning up Docker container..."
            sh 'docker stop $APP_NAME || true'
            sh 'docker rm $APP_NAME || true'
        }
    }
}
