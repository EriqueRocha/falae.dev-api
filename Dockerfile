FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre
LABEL authors="erique.dev"
WORKDIR /app
COPY --from=build /build/infrastructure/target/infrastructure-1.0.0.jar app.jar
CMD ["java", "-jar", "app.jar"]