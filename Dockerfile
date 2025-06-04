# Use a Java 21 base image (since you mentioned Java 21)
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built jar file
COPY target/*.jar app.jar

# Expose port 5000 (to match application.properties)
EXPOSE 5000

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
