# Build Stage (JAR 파일 생성)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar

# Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 빌드된 JAR 파일을 실행 스테이지로 복사
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]