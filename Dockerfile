FROM openjdk:8-jre-alpine 

RUN mkdir -p /usr/share/fairifier-backend
WORKDIR /usr/share/fairifier-backend

ADD target/fairifier-*.jar /usr/share/fairifier-backend/fairifier-backend.jar

RUN mkdir config
RUN mkdir log
VOLUME ["/usr/share/fairifier-backend/config", "/usr/share/fairifier-backend/log"]

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/fairifier-backend/fairifier-backend.jar"]
EXPOSE 8080
