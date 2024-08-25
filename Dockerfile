FROM maven:3.9-eclipse-temurin-22 AS build

# Установка рабочей директории для сборки
WORKDIR /build

# Копирование файла pom.xml и исходного кода в контейнер
COPY pom.xml .
COPY src ./src

# Сборка приложения и упаковка в JAR файл
RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-ubi9-minimal

# Создание рабочей директории для приложения
WORKDIR /app

# Копирование собранного JAR файла из этапа сборки в текущий контейнер
COPY --from=build /build/target/*.jar app.jar

# Открытие порта 8080
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]