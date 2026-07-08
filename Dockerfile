FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system factuec && adduser --system --ingroup factuec factuec
COPY --from=build /workspace/target/api-facturacion-0.1.0-SNAPSHOT.jar app.jar
USER factuec
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
