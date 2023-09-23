# Mission-point service

KYamshanov`s project for manage own tasks

Структура таблицы задачи: 
-task id == UUID (it has length equal 36 chars)
-title (50 chars)
-description (text)
-creation date-time (sql date)
-update date-time (sql date)
-completion date (can be null)
-priority
-owner (user id 36 chars)