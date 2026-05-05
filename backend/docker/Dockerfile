FROM eclipse-temurin:21 AS builder
WORKDIR /opt/app
COPY ../.mvn .mvn
COPY ../mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw -X dependency:go-offline
COPY ../src ./src
RUN ./mvnw package
RUN mv target/*.jar target/billkeeper.jar

FROM eclipse-temurin:21
RUN apt-get update && apt-get -y install tesseract-ocr
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/billkeeper.jar /opt/app/billkeeper.jar
ENTRYPOINT ["java", "-jar", "/opt/app/billkeeper.jar"]