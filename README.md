
## EXPLORE WITH ME (микросервисы)

Backend - приложение позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них.

### Cтек:
- Java 21
- Spring Boot (starters: web, validator, actuator, jpa)
- Spring Cloud (Eureka, Config, Gateway, OpenFeign)
- Maven
- DB: PostgreSQL (main), H2 (testing)
- Docker (including docker-compose)
- Lombok
- Apache Kafka

### Микросервисы:
- сore (основной модуль)
	- main-service.
	- user-service
	- request-service
	- location-service
	- like-service
	- interaction-api (общие dto, внутренние клиенты, exceptions)
- infra (модуль инфраструктуры)
	- config-server (реализация внешней конфигурации с помощью Spring Cloud Config)
	- discovery-server (реализация Service Discovery с помощью Spring Cloud Netflix Eureka)
	- gateway-server (реализация паттерна Api Gateway с помощью Spring Cloud Gateway)
- stats (модуль сбора и передачи статистики)
	- stats-client
	- stats-dto
	- stats-server

## main-service
Управление событиями (`Event`), категориями (`Category`), подбороками событий (`Compilation`).
	
`Public API`

- Получение одиночного события по id 
- Получение списка событий по заданным параметрам (содержание текста, категории, доступность, даты начала и окончания, размер)
- Получение топ-просматриваемых событий
- Получение топ-понравившихся событий


- Получение категории по id
- Получение списка категорий по параметрам


- Получение компиляции событий по id
- Получение списка компиляций по параметрам


`Private API`
	

- Создание события
- Получение собственного события по id.
- Получение списка собственных событий по параметрам
- Обновление собственного события

`Admin API`
    

- Обновление события (в том числе подтверждение)
- Получение списка событий по параметрам


- Создание категории событий
- Удаление категории событий
- Обновление категории событий


 - Создание подборки событий
 - Удаление подборки событий
 - Обновление подборки событий


`Internal API`
- Получение события по id.

## user-service
Управление пользователями (`User`)

`Admin API`

- Добавление пользователя
- Удаление пользователя
- Получение списка пользователей по id

`Internal API`
	
- Получение пользователя по id
- Проверка наличия пользователя
- Получение списка пользователей

## request-service
Управление заявками на участие в событии

`Private API`

- Создание заявки
- Отмена заявки
- Обновление статуса заявки на определенное событие
- Получение списка собственных заявок
- Получение списка заявок для собственного события


`Internal API`

- Получение количества заявок с определенным статусом события
- Получение количества заявок с определенным статусом событий

### location-service
Управление локациями

`Private API`

- Создание локации
- Получение том-понравившихся локаций

`Internal API`

- Получение локации по id
- Получение списка локаций по id

## like-service
Управление лайками событий и локаций

`Private API`

- Добавить лайк событию
- Удалить лайк события
- Добавить лайк локации
- Удалить лайк локации
	
`Internal API`	

- Получить количество лайков события
- Получить количество лайков списка событий
- Получить количество лайков локации
- Получить количество лайков самых понравившихся событий
- Получить количество лайков самых понравившихся локаций

