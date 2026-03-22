FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test
RUN echo "=== Migrations in jar ===" && jar tf build/libs/*.jar | grep migration | sort && echo "=== Total ===" && jar tf build/libs/*.jar | grep migration | wc -l

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/english-trainer-api-*.jar app.jar
COPY --from=build /app/src/main/resources/db/migration/ /app/migrations/
ENV PORT=8081
EXPOSE ${PORT}
ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=prod --server.port=${PORT} --spring.flyway.locations=classpath:db/migration,filesystem:/app/migrations"]
