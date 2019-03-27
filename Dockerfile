FROM openjdk:8-jre-alpine 

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/fairifier-backend/fairifier-backend.jar"]

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/fairifier-backend/fairifier-backend.jar

EXPOSE 8080
