package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.*

/** Репозиторий заказов (покупка, рассрочка, обработка). */
class OrderRepository(private val api: ApiService) {

    suspend fun calculate(vehicleId: Long, downPayment: Double, termMonths: Int): Result<InstallmentPlanDto> =
        safeApiCall { api.calculateInstallment(InstallmentCalcRequestDto(vehicleId, downPayment, termMonths)) }

    suspend fun buy(body: OrderRequestDto): Result<OrderDto> = safeApiCall { api.buy(body) }

    suspend fun getMine(): Result<List<OrderDto>> = safeApiCall { api.myOrders() }

    suspend fun getAll(): Result<List<OrderDto>> = safeApiCall { api.allOrders() }

    suspend fun confirm(id: Long): Result<OrderDto> = safeApiCall { api.confirmOrder(id) }

    suspend fun pay(id: Long): Result<OrderDto> = safeApiCall { api.payOrder(id) }

    suspend fun complete(id: Long): Result<OrderDto> = safeApiCall { api.completeOrder(id) }

    suspend fun cancel(id: Long): Result<OrderDto> = safeApiCall { api.cancelOrder(id) }
}
