# Доступные эндпоинты
____
- /api/auth/login (POST)
- /api/auth/register (POST)
- /api/feed (GET)
- /api/friends/approve/{username} (POST)
- /api/friends/deleteFriend/{username} (DELETE)
- /api/friends/follow/{username} (POST)
- /api/friends/myFollowers (GET)
- /api/friends/myFriends (GET)
- /api/posts/by/{username} (GET)
- /api/posts/create (POST)
- /api/posts/delete/{id} (DELETE)
- /api/posts/edit/{id} (POST)
____

# Установка
____
- Клонировать репозиторий на своё устройство
- Запустить на используемой СУБД скрипт createDatabase.sql из папки resources
- Указать в application.properties данные для работы с БД
- Вызвать сценарий maven package в папке проекта
- На выходе получится готовый jar файл, который можно запускать или деплоить на сервер
___

# Авторизация
____
Для обращения на все эндпоинты кроме авторизации, регистрации и документации необходим токен, получаемый при авторизации или регистрации.

Токен должен быть в заголовке запроса в поле Authorization
____
JSON с документацией может быть получен после запуска приложения по адресу http://{Ваш хост}:{Ваш порт}/v3/api-docs
