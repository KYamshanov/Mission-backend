FROM openjdk:17
WORKDIR /app/
ADD /build/libs/authorization-0.0.1-SNAPSHOT.jar application.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","application.jar","--debug"]