pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    environment {
        APP_NAME = "Bank"
        DOCKER_IMAGE = "bank-app:latest"
        
        // NOUVEAU: Nom de l'image Docker pour JMeter
        JMETER_DOCKER_IMAGE = "justb4/jmeter:5.6.3" 
        
        // NOUVEAU: Nom pour notre réseau Docker privé
        NETWORK_NAME = "bank-test-net"
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
                
                // NOUVEAU: On crée le réseau pour que les conteneurs puissent se parler
                sh 'docker network create $NETWORK_NAME || true'
                
                // Démarrage du conteneur avec les variables de BDD
                sh '''
                   docker run -d -p 8083:8083 \
                   --network $NETWORK_NAME \
                   -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/mydb?useSSL=false" \
                   -e SPRING_DATASOURCE_USERNAME="root" \
                   -e SPRING_DATASOURCE_PASSWORD="" \
                   --name $APP_NAME $DOCKER_IMAGE
                '''
                
                // Le script d'attente (curl) ne change pas, il vérifie toujours l'hôte
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
                echo "Running post-deployment JMeter test from Docker..."
                
                //
                // CORRECTION 1: On crée le dossier *relativement* au workspace
                //
                sh 'mkdir -p jmeter-results'
                
                //
                // CORRECTION 2: On utilise la variable ${WORKSPACE}
                // pour donner le chemin absolu au conteneur Docker JMeter.
                //
                sh """
                    docker run --rm --network $NETWORK_NAME \
                    -v "${WORKSPACE}/jmeter:/jmeter" \
                    -v "${WORKSPACE}/jmeter-results:/results" \
                    ${JMETER_DOCKER_IMAGE} \
                    -n -t /jmeter/performance_test_docker.jmx \
                    -l /results/results_docker.jtl \
                    -Jhost=$APP_NAME \
                    -Jport=8083
                """
            }
            post {
                always {
                    // Ce chemin est relatif au workspace, il est donc correct.
                    perfReport errorFailedThreshold: 5, sourceDataFiles: 'jmeter-results/results_docker.jtl'
                }
            }
        }
    } // Fin du bloc 'stages'

    post {
        always {
            echo "Cleaning up Docker container and network..."
            sh 'docker stop $APP_NAME || true'
            sh 'docker rm $APP_NAME || true'
            // NOUVEAU: On nettoie aussi le réseau
            sh 'docker network rm $NETWORK_NAME || true'
        }
    }
}