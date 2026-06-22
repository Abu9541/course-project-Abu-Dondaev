# ИС «Автосалон» — приложение (БД + сервер + Android-клиент)

Полнофункциональная клиент-серверная система автосалона по **траектории В
(мобильная разработка)** с архитектурой **PCMEF**.

```
application/
├── database/          # DDL-скрипты PostgreSQL и наполнение (этап 3 МУ)
│   ├── schema.sql     # Создание схемы (3НФ, PK/FK/UNIQUE/CHECK, индексы)
│   ├── seed.sql       # Демо-данные (для ручного развёртывания)
│   └── er-diagram.puml
├── backend/           # Сервер: Java 17 + Spring Boot (PCMEF: control/mediator/entity/foundation)
│   ├── src/main/java/ru/ncfu/autoshow/...
│   ├── Dockerfile
│   └── pom.xml
├── android/           # Клиент: Kotlin + Jetpack Compose + Retrofit + Room
│   └── app/src/main/java/ru/ncfu/autoshow/...
└── docker-compose.yml # PostgreSQL + backend одной командой
```

---

## 1. Технологический стек

| Слой | Технологии |
|------|-----------|
| Клиент (Android) | Kotlin, Jetpack Compose (Material 3), ViewModel + StateFlow, Retrofit + OkHttp, Room (оффлайн-кэш), DataStore, Coil, Navigation Compose |
| Сервер | Java 17, Spring Boot 3.2, Spring Web, Spring Data JPA (Hibernate), Spring Security, JWT (jjwt), Bean Validation, springdoc-openapi (Swagger) |
| БД | PostgreSQL 16 (3НФ) |
| Сборка / запуск | Maven, Gradle, Docker, docker-compose |
| Тесты | JUnit 5, Mockito, JaCoCo (покрытие сервиса/ядра) |

---

## 2. Запуск серверной части и БД (рекомендуется — Docker)

Требуется установленный **Docker Desktop** (запущенный).

```bash
cd application
docker compose up --build
```

Поднимаются два контейнера:
- `autoshow-db` — PostgreSQL (порт 5432);
- `autoshow-backend` — Spring Boot (порт 8080).

При первом запуске БД автоматически наполняется демо-данными
(`DataInitializer`). Проверка:

- Swagger UI / документация API: **http://localhost:8081/swagger-ui.html**
- Health-check: **http://localhost:8081/actuator/health**

Остановить: `docker compose down` (данные сохраняются в томе `pgdata`;
для полного сброса — `docker compose down -v`).

### Альтернатива без Docker
1. Установить PostgreSQL, создать БД `autoshow` (пользователь/пароль `autoshow`).
2. (опционально) применить `database/schema.sql` и `database/seed.sql`.
3. Запустить сервер: `cd backend && ./mvnw spring-boot:run`
   (или импортировать `backend` как Maven-проект в IntelliJ IDEA и запустить
   `AutoshowApplication`). Требуется JDK 17.

---

## 3. Запуск Android-клиента

1. Открыть папку **`application/android`** в Android Studio (Giraffe/Hedgehog
   или новее). Дождаться синхронизации Gradle (скачает Gradle 8.5 и SDK).
2. Убедиться, что **сервер запущен** (см. п. 2).
3. Запустить приложение на **эмуляторе Android API 26+**.
   Базовый адрес сервера уже настроен на `http://10.0.2.2:8081/`
   (`10.0.2.2` — это `localhost` хост-машины со стороны эмулятора).
   - Для запуска на **реальном устройстве** замените `BASE_URL` в
     `app/build.gradle.kts` на IP вашего компьютера в локальной сети
     (например, `http://192.168.1.10:8081/`).

### Демонстрационные учётные записи

| Роль | Email | Пароль |
|------|-------|--------|
| Администратор | `dondaevabu126@gmail.com` | `Admin@123` |
| Менеджер | `manager1@autoshow.ru` | `Manager@123` |
| Клиент | `client1@example.com` | `Client@123` |

Можно также зарегистрировать нового клиента прямо в приложении.

---

## 4. Тесты сервера и покрытие

```bash
cd backend
./mvnw test
# Отчёт о покрытии JaCoCo: target/site/jacoco/index.html
```

---

## 5. Основные возможности

- **Роли:** клиент, менеджер, администратор (разные наборы экранов и прав).
- **Каталог** с поиском, фильтрами (марка, тип кузова), пагинацией и оффлайн-кэшем.
- **Карточки автомобилей** с подробной информацией, галереей и отзывами.
- **Покупка автомобиля** (полная оплата) и **покупка в рассрочку** с калькулятором.
- **Запись на тест-драйв** с выбором дилерского центра, даты и слота.
- **Избранное**, **уведомления**, **профиль** с редактированием.
- **Менеджер:** обработка заявок (тест-драйвы, заказы), аналитика, управление каталогом (CRUD).
- **Администратор:** всё вышеперечисленное + управление пользователями (роли, блокировка).
- **Безопасность:** JWT, хеширование паролей BCrypt, ролевое разграничение.
- **Целостность и обработка ошибок:** ограничения БД, бизнес-правила в сущностях,
  глобальный обработчик исключений, полная валидация на клиенте и сервере.

Подробное соответствие требованиям методических указаний — в
[`../docs/REQUIREMENTS-COMPLIANCE.md`](../docs/REQUIREMENTS-COMPLIANCE.md).
