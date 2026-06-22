# Таблица трассировки требований

Связывает бизнес-прецеденты (BUC) → функциональные требования (FR) → системные прецеденты
(UC) → реализацию (эндпоинт/компонент) → проверку (тест/сценарий). Обеспечивает
прослеживаемость от потребности до кода.

| FR | Функциональное требование | BUC | UC | Реализация (сервер / клиент) | Проверка |
|----|---------------------------|-----|----|------------------------------|----------|
| FR-1 | Регистрация и вход по e-mail/паролю с выдачей JWT | — | UC-1 | `POST /api/auth/register`, `/login`; `AuthServiceImpl`; экран Login/Register | `AuthServiceImplTest`, `JwtServiceTest` |
| FR-2 | Просмотр каталога с поиском, фильтрацией и пагинацией | BUC-1 | UC-2 | `GET /api/vehicles`; `VehicleServiceImpl.search`; `CatalogScreen` | `VehicleServiceImplTest` |
| FR-3 | Просмотр карточки авто и отзывов | BUC-1 | UC-3 | `GET /api/vehicles/{id}`, `/{id}/reviews`; `VehicleDetailScreen` | ручной сценарий |
| FR-4 | Управление избранным | BUC-1 | UC-4 | `GET/POST/DELETE /api/favorites`; `FavoriteServiceImpl` | ручной сценарий |
| FR-5 | Запись на тест-драйв с валидацией и проверкой слота | BUC-2 | UC-5 | `POST /api/test-drives`; `TestDriveServiceImpl.book` | `TestDriveServiceImplTest` |
| FR-6 | Расчёт рассрочки (аннуитет) | BUC-3 | UC-6 | `POST /api/orders/calculate`; `InstallmentPlan.calculate` | `InstallmentPlanTest`, `OrderServiceImplTest` |
| FR-7 | Оформление покупки с резервированием авто | BUC-3 | UC-7 | `POST /api/orders`; `OrderServiceImpl.buy`; `PurchaseScreen` | `OrderServiceImplTest`, `VehicleTest` |
| FR-8 | Оплата заказа картой (имитация шлюза) | BUC-4 | UC-8 | `POST /api/payments/order/{id}`, `/{pid}/confirm`; `PaymentServiceImpl` | ручной сценарий (карта успеха/отказа) |
| FR-9 | Оставить отзыв (1–5, один на авто) | BUC-5 | UC-9 | `POST /api/vehicles/{id}/reviews`; `ReviewServiceImpl` | ручной сценарий |
| FR-10 | Отмена заявки/заказа клиентом + уведомление персоналу | BUC-3 | UC-10 | `POST /api/orders/{id}/cancel`, `/test-drives/{id}/cancel` | `OrderServiceImplTest` |
| FR-11 | Уведомления о событиях (клиенту и менеджерам) | BUC-6 | UC-11 | `GET /api/notifications`, `/unread-count`; `NotificationServiceImpl` | ручной сценарий |
| FR-12 | Профиль и настройки (тема, поддержка, соглашение) | — | UC-12 | `GET/PUT /api/users/me`; `SettingsScreen` | ручной сценарий |
| FR-13 | Обработка тест-драйвов персоналом | BUC-6 | UC-13 | `/api/test-drives/{id}/{confirm,reject,complete}` | `TestDriveServiceImplTest` |
| FR-14 | Обработка заказов персоналом (статусы) | BUC-7 | UC-14 | `/api/orders/{id}/{confirm,pay,complete,cancel}` | `OrderServiceImplTest` |
| FR-15 | Аналитическая панель | BUC-9 | UC-15 | `GET /api/dashboard`; `DashboardServiceImpl` | ручной сценарий |
| FR-16 | CRUD каталога (менеджер/админ) | BUC-8 | UC-16 | `POST/PUT/DELETE /api/vehicles`; `EditVehicleScreen` | `VehicleServiceImplTest` |
| FR-17 | Управление пользователями (роли, блокировка) | BUC-8 | UC-17 | `/api/admin/users/...`; `UsersScreen` | RBAC-проверка (403) |
| FR-18 | Список проданных авто, скрытие из каталога | BUC-8 | UC-18 | `GET /api/vehicles/sold`; фильтр каталога | `VehicleServiceImplTest` |

## Нефункциональные требования (NFR)

| NFR | Требование | Реализация |
|-----|-----------|------------|
| NFR-1 | Безопасность: JWT, RBAC, BCrypt | `SecurityConfig`, `JwtAuthenticationFilter`, `@PreAuthorize` |
| NFR-2 | Архитектура PCMEF, ацикличность зависимостей | Пакетная структура `control→mediator→entity→foundation` |
| NFR-3 | Офлайн-доступность каталога | Room-кэш (`VehicleDao`, `CachedVehicleEntity`) |
| NFR-4 | Обработка состояний UI (загрузка/ошибка/пусто) | `UiState`, `FullScreen*`-компоненты |
| NFR-5 | Документированный API | springdoc-openapi, Swagger UI |
| NFR-6 | Покрытие ядра тестами > 40 % | JUnit 5 + Mockito + JaCoCo |
| NFR-7 | Защита ПДн (152-ФЗ) | согласие на обработку (`pdnConsent`), хранение маски карты |
