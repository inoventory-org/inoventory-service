#FROM gradle:7.5.1 AS build
#COPY . /src
#WORKDIR /src
#RUN ./gradlew build -x test

FROM openjdk:17
RUN mkdir /app
COPY ./build/libs/*.jar /app/
WORKDIR /app
CMD ["ls", "."]
ENTRYPOINT ["java", "-jar", "inoventory-0.0.1-SNAPSHOT.jar"]