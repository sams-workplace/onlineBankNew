#FROM openjdk:8u212-jdk-alpine
FROM khipu/openjdk16-alpine
#COPY target/RealEstate-0.0.1-SNAPSHOT.jar app.jar
COPY target/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx400M","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=docker"]
