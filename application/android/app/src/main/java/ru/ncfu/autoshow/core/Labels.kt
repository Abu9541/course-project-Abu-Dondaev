package ru.ncfu.autoshow.core

/** Человеко-читаемые подписи для перечислений (русская локализация). */
object Labels {

    val bodyTypes = listOf(
        "SEDAN" to "Седан", "SUV" to "Внедорожник", "HATCHBACK" to "Хэтчбек",
        "COUPE" to "Купе", "WAGON" to "Универсал", "PICKUP" to "Пикап",
        "MINIVAN" to "Минивэн", "CROSSOVER" to "Кроссовер"
    )
    val engineTypes = listOf(
        "PETROL" to "Бензин", "DIESEL" to "Дизель", "HYBRID" to "Гибрид",
        "ELECTRIC" to "Электро", "GAS" to "Газ"
    )
    val transmissions = listOf(
        "MANUAL" to "Механика", "AUTOMATIC" to "Автомат", "ROBOT" to "Робот", "CVT" to "Вариатор"
    )
    val driveTypes = listOf(
        "FWD" to "Передний", "RWD" to "Задний", "AWD" to "Полный"
    )
    val vehicleStatuses = listOf(
        "IN_STOCK" to "В наличии", "RESERVED" to "Забронирован",
        "SOLD" to "Продан", "UNAVAILABLE" to "Недоступен"
    )
    val testDriveStatuses = listOf(
        "PENDING" to "Ожидает", "CONFIRMED" to "Подтверждён", "COMPLETED" to "Завершён",
        "CANCELLED" to "Отменён", "REJECTED" to "Отклонён"
    )
    val orderStatuses = listOf(
        "PENDING" to "Ожидает", "CONFIRMED" to "Подтверждён", "PAID" to "Оплачен",
        "COMPLETED" to "Завершён", "CANCELLED" to "Отменён"
    )

    private fun lookup(list: List<Pair<String, String>>, v: String?) =
        list.firstOrNull { it.first == v }?.second ?: (v ?: "—")

    fun bodyType(v: String?) = lookup(bodyTypes, v)
    fun engine(v: String?) = lookup(engineTypes, v)
    fun transmission(v: String?) = lookup(transmissions, v)
    fun drive(v: String?) = lookup(driveTypes, v)
    fun vehicleStatus(v: String?) = lookup(vehicleStatuses, v)

    fun orderStatus(v: String?) = when (v) {
        "PENDING" -> "Ожидает"; "CONFIRMED" -> "Подтверждён"; "PAID" -> "Оплачен"
        "COMPLETED" -> "Завершён"; "CANCELLED" -> "Отменён"; else -> v ?: "—"
    }

    fun testDriveStatus(v: String?) = when (v) {
        "PENDING" -> "Ожидает"; "CONFIRMED" -> "Подтверждён"; "COMPLETED" -> "Завершён"
        "CANCELLED" -> "Отменён"; "REJECTED" -> "Отклонён"; else -> v ?: "—"
    }

    fun paymentType(v: String?) = when (v) {
        "FULL" -> "Полная оплата"; "INSTALLMENT" -> "Рассрочка"; else -> v ?: "—"
    }

    fun role(v: String?) = when (v) {
        "CLIENT" -> "Клиент"; "MANAGER" -> "Менеджер"; "ADMIN" -> "Администратор"; else -> v ?: "—"
    }

    fun loyalty(v: String?) = when (v) {
        "STANDARD" -> "Стандарт"; "SILVER" -> "Серебряный"; "GOLD" -> "Золотой"
        "PLATINUM" -> "Платиновый"; else -> v ?: "—"
    }
}
