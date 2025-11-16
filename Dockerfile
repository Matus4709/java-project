############################
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .

# Copy source code
COPY src ./src

# Build application (Maven will download dependencies automatically)
# Using -U to force update of snapshots and releases
RUN mvn clean package -DskipTests -U

############################
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
