# ИС «Автосалон» — мобильное приложение

Курсовой проект по дисциплине «Программная инженерия».
**Тема:** Разработка мобильного приложения для информационной системы автосалона.
**Траектория:** В (мобильная разработка) · **Вариант:** Android Native (Kotlin).
**Архитектура:** PCMEF (Presentation–Control–Mediator–Entity–Foundation).

**Автор:** Дондаев Абу Умар-Пашаевич, группа ПИЖ-б-о-23-1, 09.03.04 «Программная инженерия», СКФУ.

---

## Что это

Клиент-серверная система автосалона: нативный Android-клиент на Kotlin/Compose,
серверная часть на Java/Spring Boot с REST API (JWT, OpenAPI) и база данных
PostgreSQL. Поддерживаются роли (клиент, менеджер, администратор), каталог
автомобилей, подробные карточки, покупка (в т.ч. в рассрочку), запись на
тест-драйв, избранное, отзывы, уведомления, аналитика и администрирование.

## Структура репозитория

```
IS Autoshow/
├── application/        # Исходный код (БД + сервер + Android-клиент)
│   ├── database/       # DDL и наполнение PostgreSQL
│   ├── backend/        # Spring Boot (PCMEF: control/mediator/entity/foundation)
│   ├── android/        # Android Native (Kotlin + Jetpack Compose)
│   └── docker-compose.yml
└── docs/               # Проектная документация (по этапам МУ)
    ├── 03-architecture/pcmef-architecture.md
    ├── 04-database/database-design.md
    └── REQUIREMENTS-COMPLIANCE.md
```

## Быстрый старт

```bash
cd application
docker compose up --build      # PostgreSQL + backend
```
Затем открыть `application/android` в Android Studio и запустить на эмуляторе
(API 26+). Подробности и демо-доступы — в
[`application/README.md`](application/README.md).

## Документация

- [Соответствие требованиям МУ](docs/REQUIREMENTS-COMPLIANCE.md)
- [Архитектура PCMEF](docs/03-architecture/pcmef-architecture.md)
- [Проектирование БД](docs/04-database/database-design.md)

---

## Статистика разработки

> Раздел заполняется на момент сдачи (из GitHub Insights). Для этого проект
> должен вестись в Git-репозитории (см. требования МУ, п. 1.2.3).

### Метрики Git
- Всего коммитов: _—_
- Период разработки: _ДД.ММ.ГГГГ – ДД.ММ.ГГГГ_

### Графики
- `docs/images/git-commit-activity.png` — активность коммитов
- `docs/images/git-punch-card.png` — распределение по дням/часам
