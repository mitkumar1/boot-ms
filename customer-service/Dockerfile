# Multi-stage build for optimized Kubernetes deployment
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install build dependencies
RUN apk add --no-cache curl maven

# Set working directory
WORKDIR /app

# Copy Maven files for dependency caching
COPY pom.xml .

# Copy source code
COPY src src

# Build the application using system Maven
RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests -B

# Runtime stage - minimal image
FROM eclipse-temurin:21-jre-alpine

# Install required packages for Kubernetes
RUN apk add --no-cache \
    curl \
    jq \
    && rm -rf /var/cache/apk/*

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Create directories for logs and config
RUN mkdir -p /app/logs /app/config && \
    chown -R appuser:appgroup /app

# Copy the JAR file from builder stage
COPY --from=builder /app/target/customer-service-*.jar app.jar

# Change ownership
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose ports
EXPOSE 4040

# Health check for Kubernetes  
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:4040/actuator/health || exit 1

# Environment variables for Kubernetes with Virtual Thread optimization
ENV SPRING_PROFILES_ACTIVE=kubernetes
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseZGC -XX:+UnlockExperimentalVMOptions --enable-preview"
ENV VIRTUAL_THREAD_OPTS="-Djdk.virtualThreadScheduler.parallelism=10 -Djdk.virtualThreadScheduler.maxPoolSize=256"
ENV JMX_OPTS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.rmi.port=9999"

# Graceful shutdown
STOPSIGNAL SIGTERM

# Run the application with Virtual Thread optimization
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS $VIRTUAL_THREAD_OPTS $JMX_OPTS -jar app.jar"]

# Labels for Kubernetes
LABEL maintainer="customer-service-team" \
      version="1.0.0" \
      description="Customer Service Microservice for Kubernetes" \
      org.opencontainers.image.source="https://github.com/your-org/customer-service" \
      org.opencontainers.image.vendor="Your Organization" \
      org.opencontainers.image.licenses="MIT"