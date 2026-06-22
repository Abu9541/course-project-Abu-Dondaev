# Соответствие требованиям методических указаний

Траектория **В (мобильная разработка)**, вариант **Android Native (Kotlin)**.

## 1. Обязательные требования траектории В

| Требование МУ | Статус | Где реализовано |
|---------------|:---:|-----------------|
| Мобильное приложение, 5+ экранов | ✅ | 18 экранов: вход, регистрация, каталог, карточка, тест-драйв, покупка, **оплата**, избранное, заявки, уведомления, профиль, **настройки**, соглашение, обработка заявок, аналитика, форма авто, пользователи, **проданные авто** (`android/.../presentation`) |
| Серверная часть на Java (Spring Boot) | ✅ | `backend/` (Spring Boot 3, Java 17) |
| REST API (8+ эндпоинтов) | ✅ | 47 эндпоинтов в `control/` (auth, vehicles, brands, test-drives, orders, **payments**, favorites, reviews, notifications, dashboard, admin) |
| Документация OpenAPI (Swagger UI) | ✅ | `springdoc-openapi`, `config/OpenApiConfig.java`, `http://localhost:8081/swagger-ui.html` |
| Аутентификация через JWT | ✅ | `security/JwtService`, `JwtAuthenticationFilter`, `SecurityConfig` |
| Локальное кэширование (оффлайн-режим) | ✅ | Room: `data/local/`, `CatalogRepository` + баннер офлайн-режима |
| Сетевое взаимодействие (Retrofit) | ✅ | `data/remote/ApiService`, `AuthInterceptor` |
| ViewModel + StateFlow | ✅ | `presentation/**/*ViewModel.kt`, `core/UiState` |
| Обработка состояний (загрузка/ошибка/пусто) | ✅ | `ui/components/CommonComponents.kt`, `UiState` |
| Модульное тестирование сервера (>40%) | ✅ | `backend/src/test` (JUnit 5 + Mockito), JaCoCo |
| Material Design, навигация | ✅ | Material 3, `navigation/NavGraph.kt` (Navigation Compose) |

## 2. Безопасность

| Требование | Статус | Где |
|------------|:---:|-----|
| JWT-токены | ✅ | `security/JwtService` |
| Хеширование паролей (BCrypt) | ✅ | `SecurityConfig.passwordEncoder()` |
| Разграничение доступа (роли) | ✅ | `CLIENT/MANAGER/ADMIN`, `@PreAuthorize`, URL-правила в `SecurityConfig` |
| Безопасное хранение токена на клиенте | ✅ | `DataStore` (`SessionManager`) |
| Защита от SQL-инъекций | ✅ | JPA/параметризованные запросы |

## 3. Архитектура PCMEF (−20% при несоответствии)

| Критерий | Статус | Где |
|----------|:---:|-----|
| Строгая иерархия слоёв P→C→M→E→F | ✅ | пакеты `control/mediator/entity/foundation` |
| Коммуникация через интерфейсы (IService/IRepository) | ✅ | `mediator/*Service` + `mediator/impl`, `foundation/*Repository` |
| Изоляция слоёв, отсутствие циклов | ✅ | [docs/02-architecture](02-architecture/dependency-diagram.md) (диаграммы пакетов и зависимостей, ацикличный граф) |
| Классы-сущности не анемичные | ✅ | бизнес-методы в `entity/` (`Vehicle.reserve()`, `Order.complete()`, `InstallmentPlan.calculate()` и др.) |

## 4. Обязательные паттерны рефакторинга

| Паттерн | Статус | Где |
|---------|:---:|-----|
| Data Mapper (обязательно) | ✅ | `mapper/*Mapper.java` + Hibernate (Entity ↔ DTO) |
| Identity Map (обязательно) | ✅ | Persistence Context (кэш L1 Hibernate) — см. ORM-стратегию |
| Lazy Load (рекомендуется) | ✅ | `FetchType.LAZY` для коллекций и связей |

## 5. База данных (этап 3)

| Требование | Статус | Где |
|------------|:---:|-----|
| Нормализация до 3НФ | ✅ | [docs/03-database/database-design.md](03-database/database-design.md) |
| PK, FK, UNIQUE, NOT NULL, CHECK | ✅ | `application/database/schema.sql` |
| Индексы для часто запрашиваемых полей | ✅ | `application/database/schema.sql` (раздел индексов) |
| ER-диаграмма, DDL, стратегия ORM | ✅ | [docs/03-database/](03-database/er-diagram.md), `application/database/er-diagram.puml` |

## 6. Целостность данных и обработка исключений

| Требование | Статус | Где |
|------------|:---:|-----|
| Ограничения целостности БД | ✅ | FK/UNIQUE/CHECK в `schema.sql` и JPA |
| Управление транзакциями | ✅ | `@Transactional` в сервисах |
| Глобальная обработка исключений | ✅ | `exception/GlobalExceptionHandler` (404/409/422/401/403/500 + JSON `ApiError`) |
| Бизнес-правила | ✅ | проверки в сущностях и сервисах (доступность авто, взнос < цены, пересечение слотов) |
| Обработка ошибок на клиенте | ✅ | `core/Network.kt` (`safeApiCall`), состояния ошибок/офлайн |

## 7. Полная валидация данных

| Уровень | Статус | Где |
|---------|:---:|-----|
| Сервер (Bean Validation) | ✅ | `@Valid` + `@NotBlank/@Email/@Positive/@Min/@Max/@Pattern/@Future` в `dto/` |
| Клиент | ✅ | проверки в ViewModel (формы входа/регистрации/покупки/авто) |
| БД | ✅ | `CHECK`/`NOT NULL`/`UNIQUE` |

## 8. Пользовательские функциональные требования

| Требование заказчика | Статус | Где |
|----------------------|:---:|-----|
| 1. Роли (админ/менеджер/пользователь) | ✅ | роли + разные наборы вкладок и прав |
| 2. Отдельные карточки авто с подробной информацией | ✅ | `presentation/detail/VehicleDetailScreen` |
| 3. Покупка автомобиля | ✅ | `presentation/purchase` + `OrderService` |
| 4. Запись на тест-драйв | ✅ | `presentation/testdrive` + `TestDriveService` |
| 5. Покупка в рассрочку | ✅ | калькулятор + `InstallmentPlan.calculate()` |
| 6. Целостность данных и обработка исключений | ✅ | см. разделы 6 |
| 7. Полная валидация данных | ✅ | см. раздел 7 |

### Дополнительный функционал (сверх минимума)
Онлайн-оплата картой (имитация платёжного шлюза: создание → подтверждение, идемпотентность),
избранное, отзывы и рейтинги, уведомления с бейджем непрочитанных, аналитическая панель,
управление каталогом (CRUD) и пользователями, страница проданных авто, настройки с
переключением темы, оффлайн-режим, уровни лояльности.

> Полный комплект проектной документации по этапам МУ — см. [docs/README.md](README.md).

## 9. Бонусные направления (МУ, ч. 3.3.5 / 5.3)
- Админ-панель (управление пользователями и каталогом) — реализована в клиенте.
- Оффлайн-режим с кэшированием — реализован (Room).
- Контейнеризация бэкенда (Docker + docker-compose) — реализована.
