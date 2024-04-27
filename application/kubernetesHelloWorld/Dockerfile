# Use the official OpenJDK image as a base image
FROM openjdk:17.0.1-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled JAR file from the Gradle build directory to the container
COPY build/libs/kubernetesHelloWorld-1.0-SNAPSHOT.jar .

# Specify the command to run your application
CMD ["java", "-jar", "kubernetesHelloWorld-1.0-SNAPSHOT.jar"]
