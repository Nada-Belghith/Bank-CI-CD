# Étape 1 : Build Maven
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copier uniquement les fichiers nécessaires pour le cache Maven
COPY pom.xml ./
COPY .mvn .mvn
RUN mvn dependency:go-offline -B

# Copier le code source et build
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copier le WAR généré
COPY --from=build /workspace/target/FirstSpringBootApplication-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/ || exit 1

ENTRYPOINT ["java","-jar","/app/app.war"]
