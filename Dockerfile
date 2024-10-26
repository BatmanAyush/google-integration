# Stage 1: Build the application
FROM gradle:7.2.0-jdk17 AS build
COPY . /home/app
WORKDIR /home/app
RUN ./gradlew clean build -x test

# Stage 2: Run the application
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /home/app/build/libs/*.jar googleSheetsAPI.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "googleSheetsAPI.jar"]
