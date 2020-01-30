FROM maven:3.6-jdk-8-alpine as build
WORKDIR /app
COPY poom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B package

FROM openjdk:8-jre-alpine
COPY --from=build /app/target/datalake-flink-1.0.0.jar /
ENTRYPOINT ["java", "-jar", "/datalake-flink-1.0.0.jar"]
