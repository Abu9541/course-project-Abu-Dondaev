# Диаграмма классов проектирования

Детальная структура классов на примере среза **«Заказы и оплата»** — показывает слои
PCMEF, интерфейсы и применённые паттерны (Data Mapper, делегирование Control → Mediator →
Foundation).

```mermaid
classDiagram
    direction TB

    class OrderController {
        +buy(principal, OrderRequest) OrderResponse
        +confirm(id, principal) OrderResponse
        +pay(id) OrderResponse
        +cancel(id, principal) OrderResponse
    }
    class PaymentController {
        +create(orderId, principal) PaymentResponse
        +confirm(pid, principal, ConfirmPaymentRequest) PaymentResponse
    }

    class OrderService {
        <<interface>>
        +buy(clientId, OrderRequest) OrderResponse
        +markPaid(id) OrderResponse
        +cancel(id, actorId, staff) OrderResponse
    }
    class PaymentService {
        <<interface>>
        +createForOrder(orderId, clientId) PaymentResponse
        +confirm(pid, clientId, req) PaymentResponse
    }
    class OrderServiceImpl {
        -OrderRepository orderRepository
        -VehicleRepository vehicleRepository
        -FavoriteRepository favoriteRepository
        -NotificationService notificationService
        -OrderMapper orderMapper
    }
    class PaymentServiceImpl {
        -PaymentRepository paymentRepository
        -OrderRepository orderRepository
        -NotificationService notificationService
        -PaymentMapper paymentMapper
        -luhnValid(digits) boolean
    }

    class Order {
        +OrderStatus status
        +confirm(manager)
        +markPaid()
        +complete()
        +cancel()
        +isInstallment() boolean
    }
    class Payment {
        +PaymentStatus status
        +succeed(maskedCard)
        +fail(maskedCard)
    }

    class OrderRepository {
        <<interface>>
        +findByUserIdOrderByCreatedAtDesc(id)
        +existsByVehicleIdAndStatusIn(...)
    }
    class PaymentRepository {
        <<interface>>
        +findByProviderPaymentId(pid)
        +findByOrderIdAndStatus(id, status)
    }
    class OrderMapper {
        +toResponse(Order) OrderResponse
    }
    class PaymentMapper {
        +toResponse(Payment) PaymentResponse
    }

    OrderController --> OrderService : uses
    PaymentController --> PaymentService : uses
    OrderServiceImpl ..|> OrderService : implements
    PaymentServiceImpl ..|> PaymentService : implements
    OrderServiceImpl --> OrderRepository : uses
    OrderServiceImpl --> OrderMapper : uses
    OrderServiceImpl --> Order : manages
    PaymentServiceImpl --> PaymentRepository : uses
    PaymentServiceImpl --> OrderRepository : uses
    PaymentServiceImpl --> PaymentMapper : uses
    PaymentServiceImpl --> Payment : manages
    Order "1" --> "0..1" InstallmentPlan
    Order "1" --> "*" Payment
    OrderMapper ..> Order
    PaymentMapper ..> Payment
```

## Замечания по проектированию

- **Слой Control** (`*Controller`) тонкий: принимает запрос, извлекает `principal` (id/роль
  из JWT), делегирует в интерфейс сервиса. Бизнес-логики не содержит.
- **Слой Mediator** (`*Service` + `*ServiceImpl`): вся логика и транзакции; реализация
  скрыта за интерфейсом (тестируется с моками репозиториев).
- **Слой Entity** (`Order`, `Payment`) — **не анемичный**: содержит бизнес-методы и
  инварианты (`order.markPaid()` проверяет, что заказ не завершён; `payment.succeed()`/
  `fail()` управляют статусом).
- **Слой Foundation** (`*Repository`) — интерфейсы Spring Data (Data Mapper).
- **Data Mapper** (`OrderMapper`, `PaymentMapper`) преобразует сущности в DTO, изолируя
  внутреннюю модель от контракта API.
