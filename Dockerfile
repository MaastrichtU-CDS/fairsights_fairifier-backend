FROM openjdk:8-jre-alpine 

ARG PORT=8080
RUN echo "port="$PORT

ENTRYPOINT ["java", "-jar", "/usr/local/fairifier-backend/fairifier-backend.jar", "--server.port="$PORT]
ADD target/*.jar /usr/local/fairifier-backend/fairifier-backend.jar

EXPOSE $PORT
