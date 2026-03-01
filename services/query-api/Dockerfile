FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/app.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]