# Financial Tracker

Серверная часть многопользовательского приложения для управления личными финансами. 
Приложение позволяет пользователям регистрироваться, добавлять, редактировать и удалять транзакции, 
отслеживать баланс, анализировать расходы и доходы, указывать долгосрочные цели и контроллировать их выполнение. 
___

Приложение включает стэк технологий:

![](img/java.png)
![](img/spring-boot.png) 
![](img/postgresql.png)
![](img/docker.png)
<img src="img/Swagger.png" width="9%" height="10%"> 
<img src="img/Liquibase.png" width="8%" height="8%">
<img src="img/spring-security.png" width="10%" height="8%">
___

Для соединения с БД используй: `user = root` и `password = password` либо изменить в `appliction.yaml`
___
Установка: `mvn clean install`

Запуск: `java -jar /path to file/FinancialTracker.jar`
___

Установка: `docker-compose up --build`
___

После запуска приложение доступно по адресу http://localhost:8080/ 

Документация доступна по адрксу http://localhost:8080/swagger-ui.html
___