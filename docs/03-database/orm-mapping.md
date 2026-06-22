# Стратегия ORM (маппинг Entity → таблицы)

Доступ к данным реализован через **Spring Data JPA (Hibernate)** — это реализация паттерна
**Data Mapper**: доменные сущности отделены от способа их хранения, ручной SQL для CRUD не
пишется.

## Общие принципы

- Базовый класс **`BaseEntity`** (`@MappedSuperclass`) содержит `id`
  (`GenerationType.IDENTITY`), `createdAt`, `updatedAt` с аудитом через `@PrePersist`/`@PreUpdate`.
- Стратегия генерации схемы: **`ddl-auto=update`** (Hibernate создаёт/обновляет схему по
  сущностям). Каноничный DDL (`schema.sql`) — эквивалентный артефакт проектирования.
- Перечисления хранятся как строки (`@Enumerated(EnumType.STRING)`) — читаемость и
  устойчивость к изменению порядка значений.
- Денежные значения — `BigDecimal` (`NUMERIC(12,2)`), без потери точности.

## Таблица соответствия

| Сущность (Entity) | Таблица | Особенности маппинга |
|-------------------|---------|----------------------|
| `User` | `users` | enum `role`, `loyaltyLevel` как STRING; `email` UNIQUE |
| `Brand` | `brands` | `name` UNIQUE |
| `Vehicle` | `vehicles` | `@ManyToOne` → Brand (EAGER); `@OneToMany` → VehicleImage (cascade ALL, orphanRemoval) |
| `VehicleImage` | `vehicle_images` | `@ManyToOne` → Vehicle; двусторонняя связь |
| `TestDrive` | `test_drives` | `@ManyToOne` → User(client), User(manager, nullable), Vehicle |
| `Order` | `orders` | `@ManyToOne` → User/Vehicle/manager; `@OneToOne` → InstallmentPlan |
| `InstallmentPlan` | `installment_plans` | `@OneToOne` → Order (FK UNIQUE), cascade ALL |
| `Payment` | `payments` | `@ManyToOne` → Order; `provider_payment_id` UNIQUE |
| `Favorite` | `favorites` | `@ManyToOne` → User/Vehicle; UNIQUE(user, vehicle) |
| `Review` | `reviews` | `@ManyToOne` → User/Vehicle; UNIQUE(user, vehicle) |
| `Notification` | `notifications` | `@ManyToOne` → User |

## Стратегии загрузки (Lazy/Eager)

- По умолчанию `@ManyToOne`/`@OneToOne` помечены **LAZY** (кроме `Vehicle.brand` — EAGER,
  т.к. марка нужна почти всегда при отображении авто) — паттерн **Lazy Load**.
- Коллекции (`@OneToMany`, напр. `Vehicle.images`) — **LAZY**, подгружаются по требованию
  в рамках транзакции.
- Чтение, отдающее данные наружу, выполняется внутри `@Transactional(readOnly = true)` —
  это гарантирует доступность ленивых связей при маппинге в DTO.

## Identity Map и транзакции

- **Identity Map** обеспечивается контекстом персистентности Hibernate (first-level cache):
  в пределах одной транзакции один и тот же ряд БД соответствует одному объекту в памяти.
- Согласованность изменений нескольких сущностей (например, при оплате: `Payment.SUCCEEDED`
  + `Order.PAID`, или при продаже: `Vehicle.SOLD` + удаление из избранного) обеспечивается
  единой транзакцией сервисного метода (`@Transactional`).

## Преобразование Entity ↔ DTO

Наружу сущности не отдаются: слой `mapper` (Data Mapper) преобразует `Entity → *Response`
и `*Request → Entity`. Это разрывает связь между внутренней моделью и контрактом API,
исключает «протечку» ленивых связей и циклических ссылок при сериализации.
