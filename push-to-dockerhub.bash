#!/bin/bash

# Проверяем, передан ли аргумент (тег)
if [ $# -eq 0 ]; then
    echo "Ошибка: не указан тег. Использование: $0 <тег>"
    exit 1
fi

TAG=$1

# Функция для обработки ошибок
handle_error() {
    echo "Ошибка, обратитесь к администратору"
    exit 1
}

# Сборка Docker образа
docker build -t 24_backend_t1 . || handle_error

# Назначение тегов
docker tag 24_backend_t1 jubastik/24_backend_t1:$TAG || handle_error
docker tag 24_backend_t1 jubastik/24_backend_t1:latest || handle_error

# Публикация образов на Docker Hub
docker push jubastik/24_backend_t1:$TAG || handle_error
docker push jubastik/24_backend_t1:latest || handle_error

echo "Операция успешно завершена"

# Запрос на обновление образа на сервере
read -p "Запустить обновление образа на сервере? [y/N] " choice
case "$choice" in
  y|Y )
    echo "Отправка запроса на обновление..."
    sleep 3
    curl -X POST "https://portainer.gortem.ru/api/stacks/webhooks/b64919f4-4c5b-4069-ab4b-a62c74ca7b68" || handle_error
    echo "Запрос на обновление отправлен успешно"
    ;;
  * )
    echo "Обновление на сервере не запущено"
    ;;
esac