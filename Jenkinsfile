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

        //
        // STAGE "Pre-Deployment" SUPPRIMÉ (Erreur n°1 corrigée)
        // Il échouait car l'application n'était pas démarrée.
        //

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
                
                //
                // CORRECTION n°2 : Le mapping de port est HOTE:CONTENEUR
                // On mappe le port 8083 de l'hôte au port 8083 du conteneur.
                //
                sh 'docker run -d -p 8083:8083 --name $APP_NAME $DOCKER_IMAGE'
                
                // Le script d'attente vise 127.0.0.1:8083 (correct)
                sh '''
                    echo "Waiting for Spring Boot to start on http://127.0.0.1:8083 ..."
                    ATTEMPTS=0
                    MAX=30
                    until curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8083/ | grep -q "200"; do
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
                //
                // CORRECTION n°3 : Le test JMeter vise le port 8083
                //
                echo "Running post-deployment JMeter test against 127.0.0.1:8083..."
                sh """
                    ${env.JMETER_HOME}/bin/jmeter -n -t jmeter/performance_test_docker.jmx -l results_docker.jtl -Jhost=127.0.0.1 -Jport=8083
                """
            }
            post {
                always {
                    // Seuil d'échec à 5% d'erreurs
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