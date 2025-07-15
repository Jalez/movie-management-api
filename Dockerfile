# syntax=docker/dockerfile:1.4

# -------- Build Stage --------
FROM gradle:jdk21-noble AS builder
WORKDIR /app

# Copy project files first
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src
COPY config ./config

# Make gradlew executable
RUN chmod +x gradlew

# Build and run tests
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon --parallel build

# -------- Runtime Stage --------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Install curl, create non-root user, and clean up
RUN apt-get update && apt-get install -y --no-install-recommends \
        curl && \
    groupadd -r spring -g 999 && \
    useradd -r -g spring -u 999 -m -d /home/spring spring && \
    rm -rf /var/lib/apt/lists/*

# Copy JAR with proper ownership
COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar
USER spring:spring

# Environment defaults
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Dspring.security.user.name=admin" \
    SPRING_PROFILES_ACTIVE=docker \
    SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    DEBUG=true

# Expose port and configure healthcheck
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]