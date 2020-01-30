FROM maven:3.6-jdk-8-alpine as build
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B package
ENTRYPOINT ["mvn", "exec:java", "-Dexec.mainClass=ru.comptech2020.EventProcessor"]
