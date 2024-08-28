# Use the official OpenJDK image as a base
FROM openjdk:17-jdk-slim


# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot JAR file to the container
COPY target/*.jar app.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8088

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]