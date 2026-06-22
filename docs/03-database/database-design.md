# Этап 3. Проектирование базы данных

СУБД: **PostgreSQL 16**. Нормализация — до **3НФ**. Целостность обеспечивается
первичными и внешними ключами, ограничениями `NOT NULL`, `UNIQUE`, `CHECK` и
индексами для часто запрашиваемых полей.

> DDL-скрипт: [`../../application/database/schema.sql`](../../application/database/schema.sql)
> Наполнение: [`../../application/database/seed.sql`](../../application/database/seed.sql)

## 1. ER-диаграмма (логическая модель)

```mermaid
erDiagram
    users ||--o{ test_drives : "оформляет"
    users ||--o{ orders : "покупает"
    users ||--o{ favorites : "избирает"
    users ||--o{ reviews : "пишет"
    users ||--o{ notifications : "получает"
    users |o--o{ test_drives : "ведёт (менеджер)"
    users |o--o{ orders : "ведёт (менеджер)"
    brands ||--o{ vehicles : "включает"
    vehicles ||--o{ vehicle_images : "имеет"
    vehicles ||--o{ test_drives : "тестируется"
    vehicles ||--o{ orders : "продаётся"
    vehicles ||--o{ favorites : ""
    vehicles ||--o{ reviews : ""
    orders ||--|| installment_plans : "детализируется"

    users {
        bigint id PK
        varchar full_name
        varchar email UK
        varchar password_hash
        varchar phone
        varchar role "CHECK CLIENT|MANAGER|ADMIN"
        varchar loyalty_level
        boolean pdn_consent
        boolean active
    }
    brands {
        bigint id PK
        varchar name UK
        varchar country
    }
    vehicles {
        bigint id PK
        bigint brand_id FK
        varchar model
        int year "CHECK 1950..2100"
        varchar vin UK
        numeric price "CHECK > 0"
        varchar body_type
        varchar engine_type
        varchar transmission
        varchar drive_type
        varchar color
        int mileage
        int power_hp
        varchar status "IN_STOCK|RESERVED|SOLD|UNAVAILABLE"
    }
    vehicle_images {
        bigint id PK
        bigint vehicle_id FK
        varchar url
        int sort_order
    }
    test_drives {
        bigint id PK
        bigint user_id FK
        bigint vehicle_id FK
        bigint manager_id FK
        varchar dealer_center
        timestamp scheduled_at
        varchar status
        varchar contact_phone
    }
    orders {
        bigint id PK
        bigint user_id FK
        bigint vehicle_id FK
        bigint manager_id FK
        varchar payment_type "FULL|INSTALLMENT"
        varchar status
        numeric total_price
    }
    installment_plans {
        bigint id PK
        bigint order_id FK
        numeric down_payment
        int term_months "CHECK 3..84"
        numeric interest_rate
        numeric monthly_payment
        numeric total_amount
    }
    favorites {
        bigint id PK
        bigint user_id FK
        bigint vehicle_id FK
    }
    reviews {
        bigint id PK
        bigint user_id FK
        bigint vehicle_id FK
        int rating "CHECK 1..5"
        varchar comment
    }
    notifications {
        bigint id PK
        bigint user_id FK
        varchar title
        varchar message
        varchar type
        boolean is_read
    }
```

## 2. Сущности (10 таблиц)

| Таблица | Назначение | Ключевые ограничения |
|---------|-----------|----------------------|
| `users` | Пользователи и роли | `email` UNIQUE; `role` CHECK; `loyalty_level` CHECK |
| `brands` | Марки автомобилей | `name` UNIQUE |
| `vehicles` | Автомобили | `vin` UNIQUE; FK→`brands`; CHECK на `year/price/power/mileage` и перечисления |
| `vehicle_images` | Фотогалерея (1:N) | FK→`vehicles` ON DELETE CASCADE |
| `test_drives` | Записи на тест-драйв | FK→`users`(клиент, менеджер), `vehicles`; `status` CHECK |
| `orders` | Заказы на покупку | FK→`users`, `vehicles`; `payment_type`/`status` CHECK; `total_price` CHECK |
| `installment_plans` | Рассрочка (1:1 к заказу) | `order_id` UNIQUE FK ON DELETE CASCADE; CHECK на срок/суммы |
| `favorites` | Избранное (N:M) | UNIQUE(`user_id`,`vehicle_id`) |
| `reviews` | Отзывы | UNIQUE(`user_id`,`vehicle_id`); `rating` CHECK 1..5 |
| `notifications` | Уведомления | FK→`users`; `type` CHECK |

Индексы созданы на внешние ключи и часто фильтруемые поля
(`vehicles.status`, `vehicles.price`, `vehicles.body_type`, статусы заявок и т.д.).

## 3. Обоснование нормализации (3НФ)

- **1НФ:** все атрибуты атомарны; повторяющиеся группы (изображения, отзывы)
  вынесены в отдельные таблицы (`vehicle_images`, `reviews`).
- **2НФ:** все таблицы имеют простой первичный ключ `id`; неключевые атрибуты
  полностью зависят от ключа.
- **3НФ:** отсутствуют транзитивные зависимости. Марка вынесена из `vehicles`
  в `brands` (нет дублирования названия/страны марки). Параметры рассрочки
  вынесены в `installment_plans` и зависят только от `order_id`.

## 4. Стратегия ORM (Entity → таблицы)

Используется **Spring Data JPA / Hibernate** (реализация паттерна **Data Mapper**).

| Приём | Реализация |
|-------|-----------|
| Сопоставление | JPA-аннотации (`@Entity`, `@Table`, `@Column`, `@ManyToOne`, `@OneToOne`, `@OneToMany`) |
| Первичные ключи | `@GeneratedValue(strategy = IDENTITY)` → `GENERATED BY DEFAULT AS IDENTITY` |
| Перечисления | `@Enumerated(EnumType.STRING)` → `VARCHAR` + `CHECK` |
| **Data Mapper** | Hibernate + явные классы-мапперы `*Mapper` (Entity ↔ DTO), отделяющие доменную модель от представления |
| **Identity Map** | Кэш первого уровня Hibernate (Persistence Context) гарантирует единственность объекта в рамках сессии |
| **Lazy Load** | `FetchType.LAZY` для коллекций (`vehicle.images`) и связей; загрузка по требованию внутри транзакции |
| Управление схемой | В приложении схему создаёт Hibernate (`ddl-auto=update`); `schema.sql` — эквивалентный канонический DDL-артефакт |

Соответствие сущностей и таблиц: пакет `ru.ncfu.autoshow.entity`
(`User`, `Brand`, `Vehicle`, `VehicleImage`, `TestDrive`, `Order`,
`InstallmentPlan`, `Favorite`, `Review`, `Notification`).
