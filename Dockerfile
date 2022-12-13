FROM gradle:7.5.1 AS build
COPY . /src
WORKDIR /src
RUN ./gradlew build

FROM openjdk:17
RUN mkdir /app
COPY --from=build /src/build/libs/*.jar /app/
WORKDIR /app
ENTRYPOINT ["java", "-jar", "inoventory-0.0.1-SNAPSHOT.jar"]