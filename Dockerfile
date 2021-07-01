FROM openjdk:8
MAINTAINER zhanpengguo
VOLUME /tmp
COPY target/jenkins-docker-demo.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]