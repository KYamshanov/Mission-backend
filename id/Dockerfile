#Build stage

FROM gradle:8.5.0-jdk17-alpine AS GRADLE
WORKDIR /usr/id/
COPY . .
RUN gradle shadowJar

# Package stage

FROM openjdk:17
ENV JAR_NAME=id-all.jar
ENV APP_HOME=/usr/id
WORKDIR $APP_HOME
COPY --from=GRADLE $APP_HOME/build/libs/$JAR_NAME .
ENTRYPOINT exec java -jar $JAR_NAME --debug