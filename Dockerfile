#STAGE 1:  BUILD
FROM maven:3.8-openjdk-17-slim as build
WORKDIR /
COPY . .
RUN mvn clean install -Dmaven.test.skip=true
RUN mv /target/lumiere_server-0.0.1-SNAPSHOT.jar /lumiere_server.jar

FROM openjdk:18-slim
COPY --from=build /lumiere_server.jar /

ENV SERVER_PORT=8080
ENV FILE_DIRECTORY=/downloadedFiles
ENV FINAL_DESTINATION_DIRECTORY=/finalDestination
#ENV FINAL_DESTINATION_CRON="0 0 0 * * ?"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "lumiere_server.jar"]