# Спецификация ключевых методов

Контракты важнейших методов бизнес-логики (слой Mediator) и бизнес-методов сущностей
(слой Entity).

## Mediator — OrderService

| Метод | Сигнатура | Поведение / побочные эффекты | Исключения |
|-------|-----------|------------------------------|------------|
| Калькулятор | `InstallmentPlanResponse calculate(InstallmentCalcRequest)` | Расчёт аннуитета без сохранения | — |
| Покупка | `OrderResponse buy(Long clientId, OrderRequest)` | Создаёт заказ `PENDING`, резервирует авто, уведомляет клиента и менеджеров | `BusinessRuleException` (авто недоступно / есть активный заказ / взнос ≥ цены) |
| Оплата (персонал) | `OrderResponse markPaid(Long id)` | Переводит заказ в `PAID` | `IllegalStateException` если заказ завершён |
| Завершение | `OrderResponse complete(Long id, Long managerId)` | `COMPLETED`, авто → `SOLD`, чистка избранного | — |
| Отмена | `OrderResponse cancel(Long id, Long actorId, boolean staff)` | `CANCELLED`, возврат авто в продажу, уведомление менеджерам при отмене клиентом | `AccessForbiddenException` (чужой заказ) |

## Mediator — PaymentService

| Метод | Сигнатура | Поведение | Исключения |
|-------|-----------|-----------|------------|
| Создание платежа | `PaymentResponse createForOrder(Long orderId, Long clientId)` | Создаёт `Payment(PENDING)`; сумма = полная цена или взнос; идемпотентно (возвращает существующий ожидающий) | `BusinessRuleException` (уже оплачен/отменён), `AccessForbiddenException` (чужой заказ) |
| Подтверждение | `PaymentResponse confirm(String providerPaymentId, Long clientId, ConfirmPaymentRequest)` | Проверка карты (Луна, срок); при успехе `SUCCEEDED` + заказ `PAID` (транзакция) + уведомления; тестовая карта → `FAILED` | `BusinessRuleException` (некорректная карта/срок), `AccessForbiddenException`, `ResourceNotFoundException` |

## Mediator — TestDriveService / NotificationService

| Метод | Сигнатура | Поведение |
|-------|-----------|-----------|
| Запись | `TestDriveResponse book(Long clientId, TestDriveRequest)` | Проверка доступности и слота; `PENDING`; уведомление клиента и менеджеров |
| Уведомление | `void notify(User user, String title, String message, NotificationType)` | Создаёт уведомление пользователю |
| Уведомление персонала | `void notifyManagers(String title, String message, NotificationType)` | Рассылает уведомление всем менеджерам |

## Entity — бизнес-методы (не анемичные сущности)

| Сущность.метод | Назначение | Инвариант |
|----------------|-----------|-----------|
| `Vehicle.reserve()` | Бронь под заказ | только из `IN_STOCK`, иначе `IllegalStateException` |
| `Vehicle.markSold()` | Перевод в «продан» | — |
| `Vehicle.returnToStock()` | Возврат в продажу при отмене | не трогает `SOLD` |
| `Vehicle.isAvailable()` | Доступность | `status == IN_STOCK` |
| `Order.markPaid()` | Перевод в `PAID` | заказ не завершён/не отменён |
| `Order.cancel()` | Отмена | нельзя отменить `COMPLETED` |
| `Payment.succeed(mask)` / `fail(mask)` | Итог платежа | хранится только маска карты |
| `InstallmentPlan.calculate(price, down, term, rate)` | Аннуитетный расчёт (фабрика) | `down < price`, платёж > 0 |
| `User.isStaff()` | Признак сотрудника | `MANAGER ∨ ADMIN` |

## Foundation — пример производных запросов

| Репозиторий.метод | Назначение |
|-------------------|-----------|
| `VehicleRepository.search(...)` | Поиск/фильтрация с пагинацией, исключение проданных |
| `VehicleRepository.findByStatus(SOLD, pageable)` | Список проданных (для админа) |
| `PaymentRepository.findByProviderPaymentId(pid)` | Поиск платежа по идентификатору шлюза |
| `TestDriveRepository.existsByVehicleIdAndScheduledAtBetweenAndStatusIn(...)` | Проверка занятости слота |
