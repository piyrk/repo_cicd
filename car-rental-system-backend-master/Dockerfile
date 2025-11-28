# Multi-stage build for Spring Boot app
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Leverage build cache: first copy only pom and resolve deps
COPY pom.xml ./
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Now copy sources and build
COPY src ./src
RUN mvn -B -DskipTests package


# Runtime image
FROM eclipse-temurin:17-jre

# App directories for file uploads (configurable via env)
ENV VARIANT_DIR=/app/files/variant-image \
    LICENSE_DIR=/app/files/license-image

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /workspace/target/*.jar /app/app.jar

# Create directories for uploads and make them writable
RUN mkdir -p "$VARIANT_DIR" "$LICENSE_DIR"

# Server port is configurable; default to 8080 for container
ENV SERVER_PORT=8080
EXPOSE 8080

# Use a dedicated non-root user when possible (optional)
# RUN useradd -ms /bin/bash appuser && chown -R appuser:appuser /app
# USER appuser

ENTRYPOINT ["java","-jar","/app/app.jar"]
