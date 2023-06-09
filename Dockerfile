FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app
COPY . /app

RUN mvn clean package

FROM openjdk:17.0.1-jdk-slim

WORKDIR /app
COPY --from=build /app/target/cryptowallet-0.0.1-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "cryptowallet-0.0.1-SNAPSHOT.jar"]
