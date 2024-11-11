ARG JAVA_VERSION=17

FROM maven:3.9.8-eclipse-temurin-${JAVA_VERSION} AS build

# Directorio de trabajo
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java${JAVA_VERSION}-debian12:nonroot AS output

WORKDIR /app

COPY --from=build /app/target/cdc-java-replication-1.0-SNAPSHOT.jar .

USER nonroot

CMD [ "cdc-java-replication-1.0-SNAPSHOT.jar" ]