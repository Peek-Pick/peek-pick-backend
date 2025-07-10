FROM eclipse-temurin:17-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

COPY .env .env
COPY src/main/resources/firebase/firebase-adminsdk.json /app/firebase/firebase-adminsdk.json
COPY src/main/resources/sentiment/pp-sentiment-analyzer-ab0839d35cd9.json /app/sentiment/pp-sentiment-analyzer-ab0839d35cd9.json

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]