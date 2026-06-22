package ru.ncfu.autoshow.data.remote.dto

// ----- Тест-драйв -----

data class TestDriveRequestDto(
    val vehicleId: Long,
    val dealerCenter: String,
    val scheduledAt: String,   // ISO LocalDateTime: yyyy-MM-dd'T'HH:mm:ss
    val contactPhone: String,
    val notes: String?
)

data class RescheduleRequestDto(
    val scheduledAt: String
)

data class TestDriveDto(
    val id: Long,
    val userId: Long?,
    val userName: String?,
    val vehicleId: Long?,
    val vehicleName: String?,
    val vehicleImageUrl: String?,
    val managerId: Long?,
    val managerName: String?,
    val dealerCenter: String,
    val scheduledAt: String,
    val status: String,
    val contactPhone: String,
    val notes: String?,
    val createdAt: String?
)

// ----- Заказы и рассрочка -----

data class InstallmentRequestDto(
    val downPayment: Double,
    val termMonths: Int
)

data class OrderRequestDto(
    val vehicleId: Long,
    val paymentType: String,            // FULL | INSTALLMENT
    val installment: InstallmentRequestDto?
)

data class InstallmentCalcRequestDto(
    val vehicleId: Long,
    val downPayment: Double,
    val termMonths: Int
)

data class InstallmentPlanDto(
    val downPayment: Double,
    val termMonths: Int,
    val interestRate: Double,
    val monthlyPayment: Double,
    val totalAmount: Double,
    val overpayment: Double
)

data class OrderDto(
    val id: Long,
    val userId: Long?,
    val userName: String?,
    val vehicleId: Long?,
    val vehicleName: String?,
    val vehicleImageUrl: String?,
    val managerId: Long?,
    val managerName: String?,
    val paymentType: String,
    val status: String,
    val totalPrice: Double,
    val installmentPlan: InstallmentPlanDto?,
    val createdAt: String?
)

// ----- Оплата (имитация платёжного шлюза) -----

data class PaymentDto(
    val id: Long,
    val providerPaymentId: String,
    val orderId: Long,
    val amount: Double,
    val method: String,
    val status: String,           // PENDING | SUCCEEDED | FAILED
    val maskedCard: String?,
    val createdAt: String?
)

data class ConfirmPaymentRequestDto(
    val cardNumber: String,       // только цифры, без пробелов
    val expiryMonth: Int,
    val expiryYear: Int,
    val cardHolder: String
)

// ----- Отзывы -----

data class ReviewRequestDto(
    val rating: Int,
    val comment: String?
)

data class ReviewDto(
    val id: Long,
    val userId: Long?,
    val userName: String?,
    val vehicleId: Long?,
    val rating: Int,
    val comment: String?,
    val createdAt: String?
)

// ----- Уведомления -----

data class NotificationDto(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    val read: Boolean,
    val createdAt: String?
)

// ----- Дашборд -----

data class DashboardDto(
    val totalVehicles: Long,
    val inStock: Long,
    val reserved: Long,
    val sold: Long,
    val totalUsers: Long,
    val totalOrders: Long,
    val pendingOrders: Long,
    val completedOrders: Long,
    val pendingTestDrives: Long,
    val totalRevenue: Double
)
