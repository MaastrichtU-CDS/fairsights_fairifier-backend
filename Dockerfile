FROM  openjdk:8-jdk

COPY target/fairifier-0.0.1-SNAPSHOT.jar /fairifier-0.0.1-SNAPSHOT.jar

EXPOSE 8080
CMD ["java", "-jar", "fairifier-0.0.1-SNAPSHOT.jar"]
