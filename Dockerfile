FROM openjdk:17.0.1-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dlog4j.debug", "-Dlog4j2.configurationFile=file:/app/ecc/sidecar-logging/log4j2.properties", "-jar","/app.jar"]