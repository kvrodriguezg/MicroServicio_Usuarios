# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
# Copiar Wallet si es necesaria para el build, o solo para runtime. Asumimos necesaria si hay tests que la usen, pero saltamos tests.
# Dejamos la copia del Wallet para el stage final.
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiar JAR generado desde el stage builder
COPY --from=builder /build/target/*.jar app.jar

# Copiar Wallet
COPY Wallet_CRYHZIE2RBKI7PDG /app/Wallet_CRYHZIE2RBKI7PDG

# Puerto
EXPOSE 9090
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dserver.port=9090", "-jar", "app.jar"]