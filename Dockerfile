FROM clojure:temurin-21-lein-alpine AS builder

WORKDIR /app

COPY project.clj /app/
RUN lein deps

COPY . /app
RUN lein uberjar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/uberjar/app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
