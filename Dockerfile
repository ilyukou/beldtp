FROM openjdk:11
ADD target/beldtp.jar beldtp.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "beldtp.jar"]