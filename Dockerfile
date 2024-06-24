FROM openjdk:18
COPY target/armin-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "armin-0.0.1-SNAPSHOT.jar"]