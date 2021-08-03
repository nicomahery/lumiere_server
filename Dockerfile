#STAGE 1:  BUILD
FROM maven:3.6.3-openjdk-11-slim as build
WORKDIR /
COPY . .
RUN mvn clean install -Dmaven.test.skip=true
RUN mv /target/lumiere_server-0.0.1-SNAPSHOT.jar /lumiere_server.jar

FROM openjdk:11.0-jre-slim
COPY --from=build /lumiere_server.jar /

ENV SERVER_PORT=8080
ENV FILE_DIRECTORY=/downloadedFiles
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "lumiere_server.jar"]