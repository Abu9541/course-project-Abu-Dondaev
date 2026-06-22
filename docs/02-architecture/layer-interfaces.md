# Спецификация интерфейсов между слоями

Слои PCMEF взаимодействуют **через интерфейсы**, а не конкретные реализации. Это
обеспечивает слабую связанность, подменяемость реализаций и изолированное тестирование
(моки в юнит-тестах).

## Control → Mediator (интерфейсы сервисов, `*Service`)

Контроллеры зависят только от интерфейсов сервисов. Реализации (`*ServiceImpl`) находятся
в пакете `mediator.impl` и помечены `@Service`/`@Transactional`.

```java
// Пример: контракт сервиса заказов (mediator/OrderService.java)
public interface OrderService {
    InstallmentPlanResponse calculate(InstallmentCalcRequest request);
    OrderResponse buy(Long clientId, OrderRequest request);
    List<OrderResponse> getMine(Long userId);
    List<OrderResponse> getAll();
    OrderResponse confirm(Long id, Long managerId);
    OrderResponse markPaid(Long id);
    OrderResponse complete(Long id, Long managerId);
    OrderResponse cancel(Long id, Long actorId, boolean staff);
}

// Контракт сервиса оплаты (mediator/PaymentService.java)
public interface PaymentService {
    PaymentResponse createForOrder(Long orderId, Long clientId);
    PaymentResponse confirm(String providerPaymentId, Long clientId, ConfirmPaymentRequest request);
}
```

Полный перечень сервисов-интерфейсов Mediator: `AuthService`, `UserService`,
`VehicleService`, `BrandService`, `TestDriveService`, `OrderService`, `PaymentService`,
`FavoriteService`, `ReviewService`, `NotificationService`, `DashboardService`.

## Mediator → Foundation (интерфейсы репозиториев, `*Repository`)

Сервисы зависят от интерфейсов репозиториев. Реализации генерирует Spring Data JPA
(паттерн **Data Mapper**) — ручной код доступа к данным не пишется.

```java
// Пример: репозиторий автомобилей (foundation/VehicleRepository.java)
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("""
        SELECT v FROM Vehicle v
        WHERE (:brandId IS NULL OR v.brand.id = :brandId)
          AND (:bodyType IS NULL OR v.bodyType = :bodyType)
          AND (:includeSold = TRUE OR v.status <> ru.ncfu.autoshow.entity.enums.VehicleStatus.SOLD)
          AND (:minPrice IS NULL OR v.price >= :minPrice)
          AND (:maxPrice IS NULL OR v.price <= :maxPrice)
          AND (:q = '' OR LOWER(v.model) LIKE LOWER(CONCAT('%', :q, '%'))
                       OR LOWER(v.brand.name) LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    Page<Vehicle> search(/* параметры фильтра */ Pageable pageable);

    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);
    boolean existsByVin(String vin);
}

// Пример: репозиторий платежей (foundation/PaymentRepository.java)
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByProviderPaymentId(String providerPaymentId);
    List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
```

Полный перечень репозиториев: `UserRepository`, `BrandRepository`, `VehicleRepository`,
`VehicleImageRepository`, `TestDriveRepository`, `OrderRepository`,
`InstallmentPlanRepository`, `PaymentRepository`, `FavoriteRepository`,
`ReviewRepository`, `NotificationRepository`.

## Клиент → Сервер (контракт REST API)

На клиенте контракт API описан Retrofit-интерфейсом `ApiService` (слой `data.remote`),
который соответствует REST-эндпоинтам контроллеров сервера. Документация контракта —
OpenAPI/Swagger UI (`/swagger-ui.html`).

```kotlin
interface ApiService {
    @POST("api/auth/login")    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto
    @GET("api/vehicles")       suspend fun vehicles(/* query-параметры */): PageDto<VehicleSummaryDto>
    @POST("api/orders")        suspend fun buy(@Body body: OrderRequestDto): OrderDto
    @POST("api/payments/order/{orderId}") suspend fun createPayment(@Path("orderId") id: Long): PaymentDto
    // ...
}
```

## Зачем это нужно

- **Подменяемость:** реализацию сервиса можно заменить, не трогая контроллеры.
- **Тестируемость:** в юнит-тестах репозитории и сервисы заменяются Mockito-моками
  (например, `@Mock OrderRepository`), что позволяет тестировать бизнес-логику изолированно.
- **Контроль зависимостей:** компиляция «снизу вверх» невозможна — нижние слои не знают о верхних.
