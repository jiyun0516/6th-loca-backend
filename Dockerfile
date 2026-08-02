# ===== 1) Build stage =====
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Gradle wrapper와 설정 파일 먼저 복사 -> 의존성 레이어 캐싱
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || return 0

# 소스 복사 후 빌드 (테스트는 배포 이미지 빌드 단계에서 생략)
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ===== 2) Run stage =====
FROM eclipse-temurin:17-jre-jammy AS run
WORKDIR /app

# non-root 유저로 실행
RUN useradd --system --create-home --shell /usr/sbin/nologin appuser
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser

# Render는 컨테이너가 $PORT를 리스닝하길 기대함 (application.properties에서 server.port=${PORT:8080} 설정됨)
EXPOSE 8080

# 컨테이너 메모리(Render 무료 512MB) 대비 힙 상한 지정
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-jar", "/app/app.jar"]
