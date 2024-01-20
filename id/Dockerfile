#Build stage

FROM gradle:8.5.0-jdk17-alpine AS GRADLE
COPY --chown=gradle:gradle . /home/gradle
RUN gradle shadowJar

# Package stage

FROM eclipse-temurin:21-jre-alpine
ENV JAR_NAME=id-all.jar
ENV APP_HOME=/app
COPY --from=GRADLE /home/gradle/build/libs/$JAR_NAME $APP_HOME/
EXPOSE 80
EXPOSE 443
ENTRYPOINT exec java -jar $APP_HOME/${JAR_NAME}