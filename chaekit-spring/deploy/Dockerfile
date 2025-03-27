# STAGE 1: Build stage
FROM gradle:7.5.1-jdk17 AS build

# Set the working directory
WORKDIR /app

# Copy the project files
COPY --chown=gradle:gradle . .

# Install dependencies and build the application
RUN gradle clean bootJar --no-daemon

# STAGE 2: Run stage
FROM openjdk:17-jdk-slim

# Set metadata
LABEL authors="jaebin"
LABEL description="chaekit"
LABEL version="1.0.0"
# Set the working directory
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]