FROM openjdk:17
COPY ./build/libs/lets-go-right-now-0.0.1-SNAPSHOT.jar lets-go-right-now.jar
ENTRYPOINT ["java", "-jar", "lets-go-right-now.jar"]