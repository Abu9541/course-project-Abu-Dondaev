package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.ConfirmPaymentRequestDto
import ru.ncfu.autoshow.data.remote.dto.PaymentDto

/** Репозиторий оплаты заказов (имитация платёжного шлюза). */
class PaymentRepository(private val api: ApiService) {

    suspend fun createForOrder(orderId: Long): Result<PaymentDto> =
        safeApiCall { api.createPayment(orderId) }

    suspend fun confirm(providerPaymentId: String, body: ConfirmPaymentRequestDto): Result<PaymentDto> =
        safeApiCall { api.confirmPayment(providerPaymentId, body) }
}
