FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container
COPY applicationJarFile/CloudflareCodingExercise-0.0.1-SNAPSHOT.jar /app/CloudflareCodingExercise-0.0.1-SNAPSHOT.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "CloudflareCodingExercise-0.0.1-SNAPSHOT.jar"]