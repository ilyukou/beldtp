FROM openjdk:13
ADD target/beldtp.jar beldtp.jar
EXPOSE 8080:8080
ENTRYPOINT ["java", "-jar", "beldtp.jar"]
ENV TZ=Europe/Minsk
RUN mkdir logs
RUN touch logs/beldtp.log