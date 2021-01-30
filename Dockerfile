FROM maven:alpine as builder
WORKDIR /app
COPY . /app
RUN mvn package


FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/check-url-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
EXPOSE 8080 8081
CMD ["java", "-cp", "/app/app.jar" , "kodampuli.Main"]
