# Use a specific version of the Gradle image as the base image
FROM gradle:8.7.0-jdk17 AS build

WORKDIR /home/gradle/src

COPY build.gradle settings.gradle gradle/ ./

COPY src ./src

COPY .editorconfig ./

RUN gradle build --no-daemon

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "/home/gradle/src/build/libs/ControlTowerPT-0.0.1-SNAPSHOT.jar"]
