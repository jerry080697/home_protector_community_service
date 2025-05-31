# 빌드 이미지로 OpenJDK 17 & Gradle을 지정
FROM gradle:8.10.2-jdk17 AS build

# Gradle 캐시 디렉토리 설정
ENV GRADLE_USER_HOME=/home/gradle/.gradle

# 소스 코드를 복사할 작업 디렉토리 생성
WORKDIR /app

# 필요한 빌드 파일만 먼저 복사 (캐싱용)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 의존성 미리 다운로드 (캐시 유지를 위해)
RUN gradle dependencies --no-daemon

# 전체 소스 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성 (테스트 제외)
RUN gradle build --no-daemon -x test

# -----------------------------------------------
# 실제 실행 환경: OpenJDK 17 slim 이미지
# 소스 코드를 복사할 작업 디렉토리를 생성
WORKDIR /app

# 필요한 파일만 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 종속성 캐시를 생성
RUN gradle dependencies --no-daemon

# 이후 전체 소스 복사 (캐시 미스가 발생하지 않으면 종속성 설치를 재실행하지 않음)
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle build --no-daemon -x test

# OpenJDK 17 기반으로 빌드
FROM openjdk:17.0.1-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar /app/community-server.jar

# 로컬에 있는 .oci 디렉토리를 컨테이너에 복사
COPY .oci /root/.oci

ENTRYPOINT ["java","-jar","/app/community-server.jar"]
