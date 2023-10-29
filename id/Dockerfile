FROM openjdk:17
WORKDIR /app/
ADD /build/libs/id-all.jar application.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","application.jar","--debug"]