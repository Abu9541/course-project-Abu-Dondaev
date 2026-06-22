# Domain Model (концептуальная модель классов)

Концептуальная модель предметной области с ключевыми атрибутами и связями. Является
основой для проектирования сущностей (слой Entity) и схемы БД.

```mermaid
classDiagram
    class User {
        +Long id
        +String fullName
        +String email
        +String phone
        +Role role
        +LoyaltyLevel loyaltyLevel
        +boolean active
    }
    class Brand {
        +Long id
        +String name
        +String country
    }
    class Vehicle {
        +Long id
        +String model
        +int year
        +String vin
        +BigDecimal price
        +BodyType bodyType
        +EngineType engineType
        +Transmission transmission
        +DriveType driveType
        +int powerHp
        +VehicleStatus status
        +String imageUrl
    }
    class VehicleImage {
        +Long id
        +String url
        +int sortOrder
    }
    class TestDrive {
        +Long id
        +String dealerCenter
        +LocalDateTime scheduledAt
        +TestDriveStatus status
        +String contactPhone
    }
    class Order {
        +Long id
        +PaymentType paymentType
        +OrderStatus status
        +BigDecimal totalPrice
    }
    class InstallmentPlan {
        +Long id
        +BigDecimal downPayment
        +int termMonths
        +BigDecimal interestRate
        +BigDecimal monthlyPayment
        +BigDecimal totalAmount
    }
    class Payment {
        +Long id
        +String providerPaymentId
        +BigDecimal amount
        +PaymentMethod method
        +PaymentStatus status
        +String maskedCard
    }
    class Favorite {
        +Long id
    }
    class Review {
        +Long id
        +int rating
        +String comment
    }
    class Notification {
        +Long id
        +String title
        +String message
        +NotificationType type
        +boolean read
    }

    Brand "1" o-- "*" Vehicle
    Vehicle "1" *-- "*" VehicleImage
    User "1" --> "*" TestDrive : client
    Vehicle "1" --> "*" TestDrive
    User "0..1" --> "*" TestDrive : manager
    User "1" --> "*" Order : client
    Vehicle "1" --> "*" Order
    User "0..1" --> "*" Order : manager
    Order "1" --> "0..1" InstallmentPlan
    Order "1" --> "*" Payment
    User "1" --> "*" Favorite
    Vehicle "1" --> "*" Favorite
    User "1" --> "*" Review
    Vehicle "1" --> "*" Review
    User "1" --> "*" Notification
```

## Перечисления (Enumerations)

| Перечисление | Значения |
|--------------|----------|
| `Role` | CLIENT, MANAGER, ADMIN |
| `LoyaltyLevel` | STANDARD, SILVER, GOLD, PLATINUM |
| `VehicleStatus` | IN_STOCK, RESERVED, SOLD, UNAVAILABLE |
| `BodyType` | SEDAN, SUV, HATCHBACK, COUPE, WAGON, PICKUP, MINIVAN, CROSSOVER |
| `EngineType` | PETROL, DIESEL, HYBRID, ELECTRIC, GAS |
| `Transmission` | MANUAL, AUTOMATIC, ROBOT, CVT |
| `DriveType` | FWD, RWD, AWD |
| `TestDriveStatus` | PENDING, CONFIRMED, COMPLETED, CANCELLED, REJECTED |
| `OrderStatus` | PENDING, CONFIRMED, PAID, COMPLETED, CANCELLED |
| `PaymentType` | FULL, INSTALLMENT |
| `PaymentMethod` | CARD, CASH |
| `PaymentStatus` | PENDING, SUCCEEDED, FAILED |
| `NotificationType` | INFO, TEST_DRIVE, ORDER, INSTALLMENT, SYSTEM |

## Инварианты модели

- `Order.totalPrice > 0`; для `INSTALLMENT` обязателен связанный `InstallmentPlan`.
- `InstallmentPlan.downPayment < Vehicle.price`.
- `Review.rating ∈ [1..5]`; пара (`User`,`Vehicle`) уникальна.
- `Favorite`: пара (`User`,`Vehicle`) уникальна.
- `Vehicle.vin` уникален; `User.email` уникален.

> Физическая модель и DDL — в [docs/03-database](../03-database/database-design.md).
> Реализация сущностей (не анемичных, с бизнес-методами) — слой Entity.
