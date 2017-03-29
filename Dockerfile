FROM openjdk:8

COPY ./ ./
RUN ./gradlew build
EXPOSE 8080
ENTRYPOINT ["./gradlew", "bootRun"]
