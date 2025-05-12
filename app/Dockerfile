
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /app

# 1. Копируем только POM файлы
COPY pom.xml .
COPY app/pom.xml ./app/
COPY audit-starter/pom.xml ./audit-starter/
COPY logging-starter/pom.xml ./logging-starter/

# 2. Настройка Maven для ускорения сборки
RUN mkdir -p /root/.m2

# 3. Скачиваем зависимости с ограничением потоков и таймаутом
RUN mvn dependency:go-offline -B \
    -T 1C \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=25

# 4. Копируем исходный код
COPY app/src ./app/src
COPY audit-starter/src ./audit-starter/src
COPY logging-starter/src ./logging-starter/src

# 5. Собираем проект с подробным выводом
RUN mvn clean package -DskipTests -B -e

# Финальный образ
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]