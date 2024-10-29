# Stage 1: Build the application
FROM gradle:7.2.0-jdk17 AS build
COPY . /home/app
WORKDIR /home/app
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# Stage 2: Run the application
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Copy the WAR file from the build stage to the app directory
COPY --from=build /home/app/build/libs/*.war app.war

# Copy the Google credentials JSON file from the resources folder into the container
COPY --from=build /home/app/src/main/resources/ivory-program-439802-u3-1e79862d03dc.json /app/ivory-program-439802-u3-1e79862d03dc.json

# Set environment variable for Google Application Credentials
ENV GOOGLE_APPLICATION_CREDENTIALS="/app/ivory-program-439802-u3-1e79862d03dc.json"

ENV PORT=8080
EXPOSE ${PORT}

# Run the WAR file using Spring Boot's embedded Tomcat engine
CMD ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT} -Djava.security.egd=file:/dev/./urandom -jar app.war"]
