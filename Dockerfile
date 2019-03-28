FROM openjdk:8-jre-alpine 

ARG PORT=8080
echo $PORT
echo ${PORT}
echo $(PORT)

echo "port=" + $PORT

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/fairifier-backend/fairifier-backend.jar", "--server.port=" + $PORT]
ADD target/*.jar /usr/share/fairifier-backend/fairifier-backend.jar

EXPOSE $PORT
