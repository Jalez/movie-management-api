# Build stage
FROM gradle:jdk21-noble AS build
WORKDIR /app

# Copy only the files needed for build first
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY src ./src
COPY config ./config

# Build with gradle
RUN ./gradlew build --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jdk-noble
WORKDIR /app

# Add curl for healthcheck
RUN apt-get update && apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/* && \
    # Create a non-root user with specific UID/GID
    groupadd -r spring -g 999 && \
    useradd -r -g spring -u 999 -m -d /home/spring spring

# Set secure file permissions
COPY --from=build --chown=spring:spring /app/build/libs/*.jar app.jar

# Switch to non-root user
USER spring:spring

# Set secure defaults
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Dspring.security.user.name=admin"
ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

# Expose API port
EXPOSE 8080

# Configure health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start application with proper shutdown handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
