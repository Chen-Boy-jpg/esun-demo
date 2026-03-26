# Stage 1: Build (編譯階段)
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
# 先下載依賴以利用 Docker 快取層
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run (執行階段)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# ✅ 修正語法：--from=build
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]