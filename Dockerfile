FROM openjdk:8-jre-alpine 

ADD target/*.jar /usr/local/fairifier-backend/fairifier-backend.jar
ENTRYPOINT java -jar /usr/local/fairifier-backend/fairifier-backend.jar

VOLUME ["/usr/local/fairifier-backend"]

EXPOSE 8080
