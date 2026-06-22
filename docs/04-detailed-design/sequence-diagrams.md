# Диаграммы последовательности

Динамика ключевых сценариев по слоям PCMEF: клиент (P) → сеть → сервер (C → M → E/F) → БД.

## 1. Аутентификация (вход по JWT)

```mermaid
sequenceDiagram
    actor U as Клиент
    participant V as LoginScreen + AuthViewModel
    participant R as AuthRepository
    participant API as ApiService (Retrofit)
    participant C as AuthController
    participant S as AuthServiceImpl
    participant Repo as UserRepository
    participant J as JwtService

    U->>V: Ввод email/пароля, «Войти»
    V->>R: login(email, password)
    R->>API: POST /api/auth/login
    API->>C: LoginRequest
    C->>S: authenticate(request)
    S->>Repo: findByEmail(email)
    Repo-->>S: User
    S->>S: BCrypt.matches(пароль)
    S->>J: generateToken(user)
    J-->>S: JWT
    S-->>C: AuthResponse(token, user)
    C-->>API: 200 OK
    API-->>R: AuthResponseDto
    R->>R: SessionManager.save(token)
    R-->>V: Result.success
    V-->>U: Переход в каталог
```

## 2. Оформление покупки и переход к оплате

```mermaid
sequenceDiagram
    actor U as Клиент
    participant V as PurchaseScreen + ViewModel
    participant R as OrderRepository
    participant C as OrderController
    participant S as OrderServiceImpl
    participant VR as VehicleRepository
    participant OR as OrderRepository(JPA)
    participant N as NotificationService

    U->>V: Выбор оплаты, «Купить»
    V->>R: buy(OrderRequest)
    R->>C: POST /api/orders
    C->>S: buy(clientId, request)
    S->>VR: findById(vehicleId)
    VR-->>S: Vehicle
    S->>S: проверка доступности и активного заказа
    S->>S: vehicle.reserve()  (RESERVED)
    S->>OR: save(order PENDING)
    S->>N: notify(client) + notifyManagers()
    S-->>C: OrderResponse(id)
    C-->>R: 201 Created
    R-->>V: Result(order.id)
    V-->>U: Переход на экран оплаты (orderId)
```

## 3. Оплата заказа (имитация платёжного шлюза)

```mermaid
sequenceDiagram
    actor U as Клиент
    participant V as PaymentScreen + ViewModel
    participant R as PaymentRepository
    participant C as PaymentController
    participant S as PaymentServiceImpl
    participant PR as PaymentRepository(JPA)
    participant N as NotificationService

    V->>R: createForOrder(orderId)
    R->>C: POST /api/payments/order/{id}
    C->>S: createForOrder(orderId, clientId)
    S->>S: проверка владельца/статуса, расчёт суммы
    S->>PR: save(Payment PENDING)
    S-->>V: PaymentResponse(amount, providerPaymentId)
    U->>V: Ввод карты, «Оплатить»
    V->>R: confirm(providerPaymentId, card)
    R->>C: POST /api/payments/{pid}/confirm
    C->>S: confirm(pid, clientId, request)
    S->>S: проверка Luhn и срока, имитация ответа банка
    alt Успех
        S->>S: payment.succeed() и order.markPaid() в транзакции
        S->>N: notify(client) + notifyManagers()
        S-->>V: status = SUCCEEDED
        V-->>U: Экран-чек «Оплата прошла»
    else Отказ (тестовая карта)
        S->>S: payment.fail()
        S-->>V: status = FAILED
        V-->>U: Ошибка, повтор
    end
```

## 4. Запись на тест-драйв с уведомлением персонала

```mermaid
sequenceDiagram
    actor U as Клиент
    participant V as TestDriveBookingScreen + ViewModel
    participant R as TestDriveRepository
    participant C as TestDriveController
    participant S as TestDriveServiceImpl
    participant TR as TestDriveRepository(JPA)
    participant N as NotificationService

    U->>V: Заполнение формы (дата/время/телефон)
    V->>V: моментальная валидация (дата не в прошлом, телефон)
    U->>V: «Записаться»
    V->>R: book(TestDriveRequest)
    R->>C: POST /api/test-drives
    C->>S: book(clientId, request)
    S->>S: проверка доступности авто
    S->>TR: existsBy...ScheduledAtBetween... (слот свободен?)
    alt Слот свободен
        S->>TR: save(TestDrive PENDING)
        S->>N: notify(client) + notifyManagers()
        S-->>C: TestDriveResponse
        C-->>V: 201 Created
        V-->>U: Подтверждение
    else Слот занят
        S-->>C: BusinessRuleException
        C-->>V: 422
        V-->>U: «Интервал занят»
    end
```
