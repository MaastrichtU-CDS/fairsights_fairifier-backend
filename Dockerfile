FROM openjdk:8-jre-alpine 

ADD target/*.jar /usr/local/fairifier-backend/fairifier-backend.jar
ENTRYPOINT java -jar /usr/local/fairifier-backend/fairifier-backend.jar --spring.profiles.active=dev

EXPOSE 8080
