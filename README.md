# ReportGenerator

**Простой сервис генерации отчётов** через HTTP API на Spring Boot.

---

## Что это делает

- Принимает POST/PUT запрос с `reportId` и списком `ReportData` (например, имя, дата, сумма).
- Создает асинхронную задачу генерации отчёта (Excel).
- Отдает статус выполнения (`PENDING`, `IN_PROGRESS`, `DONE`, `FAILED`).
- Позволяет скачать готовый файл через endpoint `/download`.

---

## Технологии

- Java 21 / Spring Boot 3.x
- Spring MVC, Spring Validation
- Jackson (JsonTypeInfo для подтипов отчетных данных)
- Apache POI для Excel
- Swagger / OpenAPI через springdoc
- JUnit 5, MockMvc, интеграционные тесты

---

## Как запустить

mvn clean package
java -jar target/ReportGenerator-0.0.1-SNAPSHOT.jar
Проект не требует внешней БД — сохраняет файлы локально в папке reports/.

## Конфигурация (application.yml)
server:
  port: 8080

report:                            # Подтипы для автоматической регистрации
  subtypes:
    - className: ru.bicev.dto.ReportDto
      name: report

## API
Submit task — PUT /api/reports
Тело JSON:

{
  "reportId": "sales-2025",
  "data": [
    {
      "type": "report", # Подтип зарегестрированный в yml
      "name": "Alice",
      "date": "2025-07-15",
      "amount": 1200.50
    }
  ]
}
Responses:
202 ACCEPTED — задача принята
400 BAD REQUEST — ошибка валидации

Get status — GET /api/reports/{reportId}
Response (200): "DONE"
Коды ошибок:
404 NOT FOUND — задачa с переданным reportId не найдена

Download — GET /api/reports/{reportId}/download
Headers:
Content-Disposition: attachment; filename=report_<reportId>.xlsx
200 OK + файл отчёта
404 NOT FOUND — файл ещё не сгенерирован или reportId неизвестен

## Документация OpenAPI (Swagger UI)
Открой браузер:
http://localhost:8080/swagger-ui.html
или
http://localhost:8080/swagger-ui/index.html

## Тесты
JUnit 5 + MockMvc для контроллера и core логики

Интеграционные тесты (@SpringBootTest) проверяют весь цикл — запрос, статус, скачивание

Поддерживается асинхронная обработка задач и валидация входных данных

## Как расширить
Добавить новые реализации ReportData с регистрацией в application.properties



