FROM gradle:8.11.1-jdk21 AS build

WORKDIR /app

COPY /app/gradle /app/gradle
COPY /app/*.gradle.kts /app/

RUN gradle dependencies --no-daemon

COPY /app/src /app/src
RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:21-jdk AS production

RUN useradd -m -u 1001 spring

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

RUN chown -R spring:spring /app

USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
