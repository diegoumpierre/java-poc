# Use Eclipse Temurin OpenJDK 17 image (multi-platform)
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy built jar from target
COPY target/auth-api-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8005

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

