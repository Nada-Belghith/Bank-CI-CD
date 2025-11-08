# Root Dockerfile — recommended for Jenkins that runs `docker build .` from repo root
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy maven wrapper and project files for cache-friendly builds
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/*.war app.war

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/ || exit 1
ENTRYPOINT ["java","-jar","/app/app.war"]
