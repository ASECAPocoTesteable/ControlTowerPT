# Use a specific version of the Gradle image as the base image
FROM gradle:8.7.0-jdk17 AS build

WORKDIR /home/gradle/src

COPY build.gradle settings.gradle gradle/ ./

COPY src ./src

COPY .editorconfig ./

RUN gradle build --no-daemon

WORKDIR /app

# Expose the port the app runs on
EXPOSE ${PORT}

# Set environment variables for the application
ENV PORT=${PORT}
ENV DATABASE_URL=${DATABASE_URL}
ENV DATABASE_USERNAME=${DATABASE_USERNAME}
ENV DATABASE_PASSWORD=${DATABASE_PASSWORD}

CMD ["java", "-jar", "/home/gradle/src/build/libs/ControlTowerPT-0.0.1-SNAPSHOT.jar"]
