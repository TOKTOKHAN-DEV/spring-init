FROM openjdk:17-alpine
LABEL authors="gunnu"

COPY ./build/libs/spring_init-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]