#Build stage

FROM gradle:8.5.0-jdk17-alpine AS GRADLE
COPY --chown=gradle:gradle . /home/gradle
RUN gradle build

# Package stage

FROM eclipse-temurin:21-jre-alpine
ENV JAR_NAME=point-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/point
COPY --from=GRADLE /home/gradle/build/libs/$JAR_NAME $APP_HOME/
EXPOSE 80
ENTRYPOINT exec java -jar $APP_HOME/${JAR_NAME}