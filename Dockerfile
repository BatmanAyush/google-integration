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

ENV PORT=8080
EXPOSE ${PORT}

# Run the WAR file using the embedded Tomcat or Spring Boot's servlet engine
CMD ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -jar app.war"]
