# Stage 1: Build the JAR (needs actual Ubuntu image for jsass, rollback if jsass is replaced)
FROM maven:3-eclipse-temurin-25 AS builder
WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime image
FROM eclipse-temurin:25-jre
RUN groupadd --system c3s && useradd --system --gid c3s c3s
WORKDIR /app
COPY --from=builder /build/target/c3s.jar app.jar
RUN chown -R c3s:c3s /app
USER c3s
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=k8s

# JVM flags tuned for containers
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
