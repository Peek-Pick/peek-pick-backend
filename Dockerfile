FROM eclipse-temurin:17-jdk-alpine

# JAR 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 포트 설정 (Spring Boot 기본 포트)
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
