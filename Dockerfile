FROM openjdk:8-jdk-alpine

RUN mkdir /build
ADD . /build
RUN chmod +x /build/mvnw
RUN cd /build && ./mvnw clean install
RUN mv /build/target/*.jar /app.jar
RUN rm -r /build
RUN rm -r /root/.m2

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
