# Stage 1: Build the application
FROM gradle:7.2.0-jdk17 AS build
COPY . /home/app
WORKDIR /home/app
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# Stage 2: Run the application
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /home/app/build/libs/*.jar app.jar

# Don't set a fixed PORT environment variable
EXPOSE 8080
CMD java -jar app.jar
