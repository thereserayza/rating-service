FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/rating-service*.jar rating-service.jar
RUN sh -c 'touch /rating-service.jar'
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "rating-service.jar" ]