FROM azul/zulu-openjdk-alpine:11
VOLUME /tmp
MAINTAINER pouya
EXPOSE 9090
ADD target/springboot-real-*.jar /springboot-real.jar
ENTRYPOINT ["java","-jar", "/springboot-real.jar"]
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar", "/springboot-real-1.0.0.jar"]