## Use a base image with JDK
#single-staged build
#FROM openjdk:17-jdk-alpine
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the Gradle wrapper and build scripts
#COPY gradlew /app/gradlew
#COPY gradle /app/gradle
#COPY build.gradle /app/build.gradle
#COPY settings.gradle /app/settings.gradle
#
## Copy the application source code
#COPY src /app/src
#
## Make the Gradle wrapper executable
#RUN chmod +x gradlew
#
## Build the application without running tests
#RUN ./gradlew build -x test
#
## Copy the built JAR file to the container
#COPY build/libs/*.jar app.jar
#
## Specify the command to run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]



# new code --- multi-staged build
# Use a base image with JDK
# First stage: build the application
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

## Copy the Gradle wrapper and build scripts
#COPY gradlew /app/gradlew
#COPY gradle /app/gradle
#COPY build.gradle /app/build.gradle
#COPY settings.gradle /app/settings.gradle
#
## Copy the application source code
#COPY src /app/src

copy ..

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Build the application without running tests
RUN ./gradlew build -x test

## Use a smaller base image for the final application
FROM eclipse-temurin:17-jre-alpine

# Copy the built JAR file to the container
COPY --from=builder /app/build/libs/*.jar app.jar

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
