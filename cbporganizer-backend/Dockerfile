FROM maven:3.6.1-jdk-8 as maven_builder

WORKDIR /backend
COPY . /backend
RUN mvn clean package

FROM openjdk:11-jre-slim

# Set the working directory to /app
WORKDIR /app

# Copy the application JAR file to the container
COPY --from=maven_builder /backend/target/*.jar /app/cbporganizer.jar

# Expose the port that the application will be listening on
EXPOSE 8080

# Run the JAR file when the container starts
CMD ["java", "-jar", "cbporganizer.jar"]
