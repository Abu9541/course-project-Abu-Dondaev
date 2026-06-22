# Диаграмма пакетов (PCMEF)

Система реализует адаптированный для клиент-серверного мобильного приложения паттерн
**PCMEF**. Зависимости направлены строго сверху вниз: **P → C → M → E → F**.

## Распределение слоёв между клиентом и сервером

```mermaid
flowchart TB
    subgraph CLIENT["📱 Мобильный клиент (Android, Kotlin)"]
        direction TB
        P["P — presentation<br/>presentation/* : Compose-экраны + ViewModel (StateFlow)"]
        DATA["api_client + local_cache<br/>data/remote (Retrofit), data/local (Room), data/repository, data/session"]
        P --> DATA
    end

    subgraph SERVER["🖥️ Сервер (Java, Spring Boot)"]
        direction TB
        C["C — control<br/>control/* : REST-контроллеры"]
        M["M — mediator<br/>mediator/* (+ impl) : сервисы, транзакции, бизнес-правила"]
        E["E — entity<br/>entity/* : сущности с бизнес-методами"]
        F["F — foundation<br/>foundation/* : репозитории (Spring Data JPA)"]
        C --> M
        M --> E
        M --> F
        F --> E
    end

    DATA -- "REST / JSON (JWT)" --> C
    F --> DB[("PostgreSQL")]
```

## Соответствие слоёв PCMEF и пакетов кода

| Слой PCMEF | Сторона | Пакет / модуль | Ответственность |
|------------|---------|----------------|-----------------|
| **Presentation** | Клиент | `ru.ncfu.autoshow.presentation.*`, `navigation`, `ui` | Экраны Compose, ViewModel, навигация, тема |
| *(api_client / local_cache)* | Клиент | `data.remote`, `data.local`, `data.repository`, `data.session`, `data.settings` | Retrofit-API, Room-кэш, репозитории, сессия/настройки |
| **Control** | Сервер | `control` | Приём HTTP-запросов, валидация DTO, делегирование |
| **Mediator** | Сервер | `mediator` (+ `mediator.impl`) | Бизнес-логика, транзакции, правила |
| **Entity** | Сервер | `entity` (+ `entity.enums`) | Сущности с бизнес-методами (не анемичные) |
| **Foundation** | Сервер | `foundation` | Репозитории доступа к данным |
| *(вспомогательные)* | Сервер | `dto`, `mapper`, `security`, `exception`, `config` | DTO, Data Mapper, JWT/RBAC, обработка ошибок, конфигурация |

## Принципы соблюдения PCMEF

1. **Строгая иерархия:** контроллеры не обращаются к репозиториям напрямую — только через
   сервисы Mediator; репозитории не содержат бизнес-логики.
2. **Коммуникация через интерфейсы:** Control зависит от интерфейсов сервисов
   (`*Service`), Mediator — от интерфейсов репозиториев (Spring Data). См.
   [layer-interfaces.md](layer-interfaces.md).
3. **Изоляция представления:** Presentation (клиент) общается с сервером только по REST,
   не зная о внутренней структуре сервера.
4. **Отсутствие циклов:** граф зависимостей ацикличен — см.
   [dependency-diagram.md](dependency-diagram.md).

> Подробное описание ответственности слоёв и обоснование — в
> [pcmef-architecture.md](pcmef-architecture.md) и [adr.md](adr.md).
