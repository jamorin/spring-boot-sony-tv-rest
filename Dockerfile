FROM openjdk:8-jdk-alpine

RUN mkdir /build
ADD . /build
RUN cd /build && ./mvnw clean install
RUN mv /build/target/*.jar /app.jar
RUN rm -r /build
RUN rm -r /root/.m2

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
