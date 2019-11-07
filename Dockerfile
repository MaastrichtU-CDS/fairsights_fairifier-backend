FROM openjdk:8-jre-alpine 

RUN mkdir -p /app/fairifier-backend
WORKDIR /app/fairifier-backend

ADD target/fairifier-*.jar /app/fairifier-backend/fairifier-backend.jar

RUN mkdir config
RUN mkdir log
VOLUME ["/app/fairifier-backend/config", "/app/fairifier-backend/log"]

ENTRYPOINT ["/usr/bin/java", "-jar", "/app/fairifier-backend/fairifier-backend.jar"]
EXPOSE 8080
