# Fase 1: Build con Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . /app
RUN mvn clean package spring-boot:repackage -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/poc-poleepo-web/target/*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-Dspring.profiles.active=main", "-jar", "app.jar"]
