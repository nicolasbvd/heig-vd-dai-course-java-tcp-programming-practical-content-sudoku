# Use Temurin Java 21 as base image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

COPY target/java-tcp-programming-1.0-SNAPSHOT.jar /app/java-tcp-programming-1.0-SNAPSHOT.jar

# Expose the required port
EXPOSE 1236

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/java-tcp-programming-1.0-SNAPSHOT.jar"]

#To run client on localhost: docker run -it --network="host" sudoku-app client --host=127.0.0.1
#To run server: docker run -p 1236:1236 sudoku-app server

#Dont forget to build: docker build -t sudoku-app .