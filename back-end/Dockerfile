# Use the official Eclipse Temurin image as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set the application JAR file
ARG JAR_FILE=target/back-end-0.0.1-SNAPSHOT.jar

# Copy the application's JAR file to the container
COPY ${JAR_FILE} app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app.jar"]
