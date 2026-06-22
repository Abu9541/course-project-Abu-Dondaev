package ru.ncfu.autoshow.presentation.payment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.data.remote.dto.ConfirmPaymentRequestDto
import ru.ncfu.autoshow.data.repository.PaymentRepository
import java.time.YearMonth

/** ViewModel экрана оплаты заказа (имитация платёжного шлюза). */
class PaymentViewModel(
    private val orderId: Long,
    private val repo: PaymentRepository
) : ViewModel() {

    var loading by mutableStateOf(true)
        private set
    var amount by mutableStateOf<Double?>(null)
        private set
    var loadError by mutableStateOf<String?>(null)
        private set

    // Поля формы карты
    var cardNumber by mutableStateOf("")   // сгруппированный «#### #### #### ####»
    var expiry by mutableStateOf("")       // ММ/ГГ
    var cvc by mutableStateOf("")
    var cardHolder by mutableStateOf("")

    var processing by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var succeeded by mutableStateOf(false)
        private set

    private var providerPaymentId: String? = null

    init { create() }

    private fun create() {
        viewModelScope.launch {
            loading = true
            repo.createForOrder(orderId)
                .onSuccess { providerPaymentId = it.providerPaymentId; amount = it.amount }
                .onFailure { loadError = it.message ?: "Не удалось создать платёж" }
            loading = false
        }
    }

    private val cardDigits: String get() = cardNumber.filter { it.isDigit() }

    val cardValid: Boolean get() = cardDigits.length in 13..19 && luhn(cardDigits)
    val expiryValid: Boolean get() = Regex("^\\d{2}/\\d{2}$").matches(expiry) && expiryNotPast()
    val cvcValid: Boolean get() = cvc.length in 3..4 && cvc.all { it.isDigit() }
    val holderValid: Boolean get() = cardHolder.isNotBlank()
    val canPay: Boolean get() = cardValid && expiryValid && cvcValid && holderValid && !processing

    fun pay() {
        val pid = providerPaymentId ?: return
        if (!canPay) return
        val month = expiry.substring(0, 2).toInt()
        val year = 2000 + expiry.substring(3, 5).toInt()
        viewModelScope.launch {
            processing = true; error = null
            repo.confirm(pid, ConfirmPaymentRequestDto(cardDigits, month, year, cardHolder.trim()))
                .onSuccess {
                    when (it.status) {
                        "SUCCEEDED" -> succeeded = true
                        "FAILED" -> error = "Банк отклонил операцию. Попробуйте другую карту."
                        else -> error = "Платёж не завершён, попробуйте снова."
                    }
                }
                .onFailure { error = it.message }
            processing = false
        }
    }

    private fun expiryNotPast(): Boolean {
        val month = expiry.substring(0, 2).toIntOrNull() ?: return false
        val yy = expiry.substring(3, 5).toIntOrNull() ?: return false
        if (month !in 1..12) return false
        return !YearMonth.of(2000 + yy, month).isBefore(YearMonth.now())
    }

    /** Проверка номера карты по алгоритму Луна (как и на сервере). */
    private fun luhn(digits: String): Boolean {
        var sum = 0
        var alt = false
        for (i in digits.indices.reversed()) {
            var d = digits[i] - '0'
            if (alt) { d *= 2; if (d > 9) d -= 9 }
            sum += d
            alt = !alt
        }
        return sum % 10 == 0
    }
}
