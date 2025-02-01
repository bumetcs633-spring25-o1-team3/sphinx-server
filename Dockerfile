# Stage 1: Build the application
# We use Gradle JDK 17 image for building to ensure consistent build environment
FROM gradle:7-jdk17 AS builder

# Set the working directory to /app
WORKDIR /app

# Copy the Gradle configuration files first
# This is done separately from the source code to leverage Docker layer caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy the source code
COPY src ./src

# Build the application without running tests
# --no-daemon reduces memory usage during build
RUN gradle build --no-daemon -x test

# Stage 2: Create the runtime image
# We use a minimal JRE image to reduce the final image size
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy only the built JAR from the builder stage
# The --from=builder flag specifies we want to copy from the previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Create a non-root user for security
RUN useradd -m myuser
USER myuser

# Document which port the application uses
EXPOSE 8085

# Start the application
# We use array syntax for better signal handling
CMD ["java", "-jar", "app.jar"]