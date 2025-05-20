# Build stage
FROM azul/zulu-openjdk-alpine:16-jdk AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /build

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package

# Runtime stage
FROM azul/zulu-openjdk-alpine:16-jre-headless

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /build/target/admission-1.0-SNAPSHOT.jar ./app.jar

# Create directory for SQLite database and set permissions
RUN mkdir -p /app/data && \
    chown -R appuser:appgroup /app && \
    # Clean up any unnecessary files
    rm -rf /var/cache/apk/* && \
    rm -rf /tmp/*

# Switch to non-root user
USER appuser

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application with minimal memory footprint
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-XX:MinRAMPercentage=25.0", \
    "-jar", "app.jar"] 