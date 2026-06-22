package ru.ncfu.autoshow.presentation.purchase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.data.remote.dto.InstallmentRequestDto
import ru.ncfu.autoshow.data.remote.dto.InstallmentPlanDto
import ru.ncfu.autoshow.data.remote.dto.OrderRequestDto
import ru.ncfu.autoshow.data.remote.dto.VehicleDto
import ru.ncfu.autoshow.data.repository.CatalogRepository
import ru.ncfu.autoshow.data.repository.OrderRepository
import kotlin.math.roundToLong

/** ViewModel оформления покупки (полная оплата или рассрочка с калькулятором). */
class PurchaseViewModel(
    private val vehicleId: Long,
    private val catalogRepo: CatalogRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    var vehicle by mutableStateOf<VehicleDto?>(null)
        private set
    var loadError by mutableStateOf<String?>(null)
        private set

    var paymentType by mutableStateOf("FULL")
        private set
    var downPaymentText by mutableStateOf("")
    var termMonths by mutableStateOf(36)
        private set

    var plan by mutableStateOf<InstallmentPlanDto?>(null)
        private set
    var calculating by mutableStateOf(false)
        private set
    var submitting by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            catalogRepo.getVehicle(vehicleId)
                .onSuccess {
                    vehicle = it
                    downPaymentText = (it.price * 0.2).roundToLong().toString()
                }
                .onFailure { loadError = it.message }
        }
    }

    fun selectPaymentType(type: String) {
        paymentType = type
        if (type == "INSTALLMENT") recalculate()
    }

    fun setTerm(months: Int) {
        termMonths = months
        recalculate()
    }

    private fun parsedDownPayment(): Double? =
        downPaymentText.filter { it.isDigit() || it == '.' }.toDoubleOrNull()

    fun recalculate() {
        val down = parsedDownPayment() ?: return
        viewModelScope.launch {
            calculating = true; error = null
            orderRepo.calculate(vehicleId, down, termMonths)
                .onSuccess { plan = it }
                .onFailure { error = it.message; plan = null }
            calculating = false
        }
    }

    fun buy(onSuccess: (Long) -> Unit) {
        val request = if (paymentType == "INSTALLMENT") {
            val down = parsedDownPayment()
            if (down == null) { error = "Укажите первоначальный взнос"; return }
            OrderRequestDto(vehicleId, "INSTALLMENT", InstallmentRequestDto(down, termMonths))
        } else {
            OrderRequestDto(vehicleId, "FULL", null)
        }
        viewModelScope.launch {
            submitting = true; error = null
            orderRepo.buy(request)
                .onSuccess { onSuccess(it.id) }
                .onFailure { error = it.message }
            submitting = false
        }
    }

    fun clearError() { error = null }

    companion object {
        val TERMS = listOf(12, 24, 36, 48, 60)
    }
}
