FROM gradle:8.10.2-jdk17 AS build

WORKDIR /app

# gradle wrapper 및 설정 파일만 먼저 복사
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 캐시 생성 (소스코드 복사 전)
RUN ./gradlew dependencies --no-daemon || return 0

# 전체 소스 복사
COPY . .

# 빌드 실행 (테스트 제외 가능: -x test)
RUN ./gradlew build --no-daemon

FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/guide-server.jar

ENTRYPOINT ["java","-jar","/app/guide-server.jar"]
