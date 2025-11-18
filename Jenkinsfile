pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    environment {
        APP_NAME = "Bank"
        DOCKER_IMAGE = "bank-app:latest"
        JMETER_DOCKER_IMAGE = "justb4/jmeter:latest" 
        NETWORK_NAME = "bank-test-net"
        SONARQUBE = 'SonarQube'

    }

    stages {

        //
        // ÉTAPE CORRIGÉE ET SÉCURISÉE
        //
        stage('Checkout') {
            steps {
                // Cette étape est sécurisée.
                // Elle utilise l'identifiant que vous avez enregistré
                // dans le gestionnaire de 'Credentials' de Jenkins.
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Nada-Belghith/Bank-CI-CD.git',
                        
                        // Assurez-vous que cet ID correspond à celui
                        // que vous avez créé dans Jenkins
                        credentialsId: 'gitid' 
                    ]]
                ])
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        // ...
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv(env.SONARQUBE_SERVER) {
            withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {

                // VÉRIFIEZ CETTE LIGNE - ce doit être 'sh'
                sh "mvn sonar:sonar -Dsonar.login=${SONAR_TOKEN}"

            }
        }
    }
}
// ...

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
                sh 'docker stop bank-mysql || true'
                sh 'docker rm bank-mysql || true'
                sh 'docker network create $NETWORK_NAME || true'

                // Démarrer un conteneur MySQL
                sh '''
                    docker run -d --name bank-mysql --network $NETWORK_NAME \
                      -e MYSQL_ROOT_PASSWORD=root \
                      -e MYSQL_DATABASE=bank_db \
                      mysql:8.0
                '''

                // Attendre que MySQL soit prêt
                sh '''
                    echo "Waiting for MySQL (bank-mysql) to be ready..."
                    ATT=0
                    MAX=30
                    until docker logs bank-mysql 2>&1 | grep -q "ready for connections"; do
                      ATT=$((ATT+1))
                      if [ "$ATT" -ge "$MAX" ]; then
                        echo "MySQL did not start in time"
                        docker logs bank-mysql || true
                        exit 1
                      fi
                      sleep 2
                      echo "Waiting for MySQL... ($ATT)"
                    done
                    echo "MySQL ready"
                '''

                // Démarrer l'application Spring Boot
                sh '''
                    docker run -d -p 8083:8083 --network $NETWORK_NAME \
                      -e SPRING_DATASOURCE_URL="jdbc:mysql://bank-mysql:3306/bank_db?useSSL=false&allowPublicKeyRetrieval=true" \
                      -e SPRING_DATASOURCE_USERNAME="root" \
                      -e SPRING_DATASOURCE_PASSWORD="root" \
                      -e SERVER_PORT=8083 \
                      --name $APP_NAME $DOCKER_IMAGE
                '''

                // Attendre que l'application réponde
                sh '''
                    echo "Waiting for Spring Boot to start on http://127.0.0.1:8083 ..."
                    ATTEMPTS=0
                    MAX=60
                    until curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8083/ | grep -E -q "[234].."; do
                        ATTEMPTS=$((ATTEMPTS+1))
                        if [ "$ATTEMPTS" -ge "$MAX" ]; then
                          echo "Timed out waiting for application to start"
                          echo "--- APP DOCKER LOGS ---"
                          docker logs $APP_NAME || true
                          echo "--- MYSQL DOCKER LOGS ---"
                          docker logs bank-mysql || true
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
                
                sh 'mkdir -p jmeter-results'
                
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
                    perfReport errorFailedThreshold: 5, sourceDataFiles: 'jmeter-results/results_docker.jtl'
                }
            }
        }
    } // Fin du bloc 'stages'

    post {
        always {
            echo "Cleaning up Docker container and network..."
            // CORRECTION NETTOYAGE: On arrête les DEUX conteneurs
            sh 'docker stop $APP_NAME || true'
            sh 'docker rm $APP_NAME || true'
            sh 'docker stop bank-mysql || true'
            sh 'docker rm bank-mysql || true'
            sh 'docker network rm $NETWORK_NAME || true'
        }
    }
}