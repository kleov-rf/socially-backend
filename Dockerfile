FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

RUN addgroup -S spring && adduser -S spring -G spring

COPY app/build/libs/*.jar app.jar

RUN wget -O dd-java-agent.jar "https://dtdg.co/latest-java-tracer"

RUN chown spring:spring app.jar dd-java-agent.jar

USER spring:spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT [
    "java",
    "-javaagent:dd-java-agent.jar",
    "-Ddd.profiling.enabled=true",
    "-XX:+UseContainerSupport",
    "-XX:MaxRAMPercentage=75.0",
    "-Djava.security.egd=file:/dev/./urandom",
    "-jar",
    "app.jar"
]
