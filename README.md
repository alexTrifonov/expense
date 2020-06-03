# expense
Приложение для учета расходов

SPA Web-приложение с web-интерфейсом для управления расходами на базе Spring MVC, Vue.js. Размещается на сервере приложений.
В качестве базы данных используется PostgreSQL 12.
В качестве реализации спецификации JPA используется Hibernate.
Для использования приложения необходимо в кластере PostgresSQL:
- создать пользователя expense.
- создать базу данных expense_db и присвоить ей владельца expense.
- в базе данных expense_db создать схему expense.
- создать таблицы category, expense, скрипт создания расположен в expense-backend\src\main\resources\schema.sql

Путь к директории на сервере с развернутым приложением используется 
в router\index.js в свойстве base для router
и в настройках доступа к api в components\http-common.js в baseURL