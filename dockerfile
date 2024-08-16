FROM gradle:8.10.0-jdk17-alpine AS BUILD
WORKDIR /usr/app/
COPY . . 
RUN gradle build

FROM amazoncorretto:17-alpine3.20

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT exec java -jar /app/spring-boot-application.jar