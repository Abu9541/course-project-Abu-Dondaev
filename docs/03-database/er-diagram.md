# ER-диаграмма (логическая модель данных)

Схема нормализована до 3НФ. Целостность обеспечивается PK, FK, UNIQUE, NOT NULL и CHECK.
Диаграмма рендерится прямо на GitHub (Mermaid). Каноничный DDL — в
[`application/database/schema.sql`](../../application/database/schema.sql); исходник для
PlantUML — [`application/database/er-diagram.puml`](../../application/database/er-diagram.puml).

```mermaid
erDiagram
    USERS ||--o{ TEST_DRIVES : "client"
    USERS ||--o{ ORDERS : "client"
    USERS ||--o{ FAVORITES : ""
    USERS ||--o{ REVIEWS : ""
    USERS ||--o{ NOTIFICATIONS : ""
    BRANDS ||--o{ VEHICLES : "включает"
    VEHICLES ||--o{ VEHICLE_IMAGES : ""
    VEHICLES ||--o{ TEST_DRIVES : ""
    VEHICLES ||--o{ ORDERS : ""
    VEHICLES ||--o{ FAVORITES : ""
    VEHICLES ||--o{ REVIEWS : ""
    ORDERS ||--o| INSTALLMENT_PLANS : "имеет"
    ORDERS ||--o{ PAYMENTS : "оплачивается"

    USERS {
        bigint id PK
        varchar email UK
        varchar password_hash
        varchar full_name
        varchar role
        varchar loyalty_level
        boolean active
    }
    BRANDS {
        bigint id PK
        varchar name UK
        varchar country
    }
    VEHICLES {
        bigint id PK
        bigint brand_id FK
        varchar model
        int year
        varchar vin UK
        numeric price
        varchar body_type
        varchar status
    }
    VEHICLE_IMAGES {
        bigint id PK
        bigint vehicle_id FK
        varchar url
        int sort_order
    }
    TEST_DRIVES {
        bigint id PK
        bigint user_id FK
        bigint vehicle_id FK
        bigint manager_id FK
        timestamp scheduled_at
        varchar status
    }
    ORDERS {
        bigint id PK
        bigint user_id FK
        bigint vehicle_id FK
        bigint manager_id FK
        varchar payment_type
        varchar status
        numeric total_price
    }
    INSTALLMENT_PLANS {
        bigint id PK
        bigint order_id FK
        numeric down_payment
        int term_months
        numeric monthly_payment
    }
    PAYMENTS {
        bigint id PK
        bigint order_id FK
        varchar provider_payment_id UK
        numeric amount
        varchar status
        varchar masked_card
    }
    FAVORITES {
        bigint id PK
        bigint user_id FK
        bigint vehicle_id FK
    }
    REVIEWS {
        bigint id PK
        bigint user_id FK
        bigint vehicle_id FK
        int rating
    }
    NOTIFICATIONS {
        bigint id PK
        bigint user_id FK
        varchar type
        boolean is_read
    }
```

## Перечень таблиц (11)

| Таблица | Назначение | Ключевые ограничения |
|---------|-----------|----------------------|
| `users` | Пользователи и роли | `email` UNIQUE; CHECK на роль/лояльность |
| `brands` | Марки | `name` UNIQUE |
| `vehicles` | Автомобили | `vin` UNIQUE; FK→brands; CHECK цена/год/статус |
| `vehicle_images` | Фотогалерея | FK→vehicles (ON DELETE CASCADE) |
| `test_drives` | Записи на тест-драйв | FK→users/vehicles/manager; CHECK статуса |
| `orders` | Заказы | FK→users/vehicles/manager; CHECK тип/статус/цена |
| `installment_plans` | Планы рассрочки | `order_id` UNIQUE (1:1); FK→orders |
| `payments` | Платежи | `provider_payment_id` UNIQUE; FK→orders; CHECK метод/статус |
| `favorites` | Избранное (N:M) | UNIQUE (user_id, vehicle_id) |
| `reviews` | Отзывы | UNIQUE (user_id, vehicle_id); CHECK rating 1..5 |
| `notifications` | Уведомления | FK→users; CHECK типа |

## Индексы

Созданы для часто фильтруемых/соединяемых полей: `vehicles(brand_id, status, price, body_type)`,
`test_drives(user_id, vehicle_id, status, scheduled_at)`, `orders(user_id, vehicle_id, status)`,
`payments(order_id)`, `favorites(user_id)`, `reviews(vehicle_id)`,
`notifications(user_id, is_read)`. Полный список — в `schema.sql`.
