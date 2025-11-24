FROM amazoncorretto:17-alpine

WORKDIR /app

# 1. Gradle 관련 메타파일만 먼저 복사 → 캐시 유지 목적
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# 2. Gradle wrapper 실행 권한
RUN chmod +x gradlew

# 3. 의존성만 먼저 받아 캐시 (변하지 않으면 매번 재사용)
RUN ./gradlew dependencies || true

# 4. 소스 코드 COPY (변경 잦으므로 최하단)
COPY src/ src/

# 5. 실제 빌드 (한 번만)
RUN ./gradlew clean build -x test

CMD ["java", "-Duser.timezone=Asia/Seoul", "-Xms64m", "-Xmx128m", "-XX:+UseSerialGC", "-XX:MaxMetaspaceSize=256m", "-XX:+ExitOnOutOfMemoryError", "-jar", "build/libs/chaekit-0.0.1-SNAPSHOT.jar"]
