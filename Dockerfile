# ==========================================
# Stage 1: Builder
# ==========================================
FROM eclipse-temurin:22-jdk-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

#  Copy the Gradle wrapper and settings first
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Ensure the wrapper script is executable
RUN chmod +x ./gradlew

# Fetch dependencies to cache them in a dedicated Docker layer.
RUN ./gradlew dependencies --no-daemon || true

# Copy the rest of the source code
COPY src src

# Build the application (skipping tests to speed up the image build)
RUN ./gradlew assemble -x test --no-daemon

# ==========================================
# Stage 2: Runtime
# ==========================================
# Use the JRE for a significantly smaller and more secure runtime image
FROM eclipse-temurin:22-jre-alpine

WORKDIR /app

# Create a non-root user and group for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup

# Copy the compiled "fat jar" from the builder stage.
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your backend runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]