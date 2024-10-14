# Use a base image with OpenJDK
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/demo-1.0-SNAPSHOT.jar /app/demo-1.0-SNAPSHOT.jar

# Run the JAR file
CMD ["java", "-jar", "demo-1.0-SNAPSHOT.jar"]
