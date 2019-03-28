FROM openjdk:8-jre-alpine 

ARG PORT=8080

ADD target/*.jar /usr/local/fairifier-backend/fairifier-backend.jar
ENTRYPOINT java -jar /usr/local/fairifier-backend/fairifier-backend.jar --server.port="$PORT"

EXPOSE $PORT
