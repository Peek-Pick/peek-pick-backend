FROM eclipse-temurin:17-jdk-alpine

# JAR 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 포트 설정 (Spring Boot 기본 포트)
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

COPY .env .env

# Dockerfile 내에 아래 라인 추가
COPY src/main/resources/firebase/firebase-adminsdk.json /app/firebase/firebase-adminsdk.json
COPY src/main/resources/sentiment/pp-sentiment-analyzer-ab0839d35cd9.json /app/sentiment/pp-sentiment-analyzer-ab0839d35cd9.json

