FROM openjdk:8-jre-alpine 

ADD target/*.jar /usr/local/fairifier-backend/fairifier-backend.jar
ENTRYPOINT java -jar /usr/local/fairifier-backend/fairifier-backend.jar

EXPOSE 8080
