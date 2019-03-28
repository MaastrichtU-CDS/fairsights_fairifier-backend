FROM openjdk:8-jre-alpine 

ARG PORT=8080
RUN echo $PORT
RUN echo ${PORT}
RUN echo $(PORT)

RUN echo "port=" + $PORT

ENTRYPOINT ["java", "-jar", "/usr/share/fairifier-backend/fairifier-backend.jar", "--server.port=" + $PORT]
ADD target/*.jar /usr/share/fairifier-backend/fairifier-backend.jar

EXPOSE $PORT
