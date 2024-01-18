#Build stage

FROM gradle:8.5.0-jdk17-alpine AS GRADLE
COPY --chown=gradle:gradle . /home/gradle
RUN gradle build

# Package stage

FROM openjdk:17
ENV JAR_NAME=gateway-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/gateway
COPY --from=GRADLE /home/gradle/build/libs/$JAR_NAME $APP_HOME/
WORKDIR $APP_HOME
ENTRYPOINT exec java -jar ${JAR_NAME} --debug