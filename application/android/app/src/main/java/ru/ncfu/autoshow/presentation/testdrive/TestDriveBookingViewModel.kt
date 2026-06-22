package ru.ncfu.autoshow.presentation.testdrive

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.data.remote.dto.TestDriveRequestDto
import ru.ncfu.autoshow.data.repository.CatalogRepository
import ru.ncfu.autoshow.data.repository.TestDriveRepository
import java.time.LocalDateTime

/** ViewModel записи на тест-драйв. */
class TestDriveBookingViewModel(
    private val vehicleId: Long,
    catalogRepo: CatalogRepository,
    private val testDriveRepo: TestDriveRepository
) : ViewModel() {

    var vehicleName by mutableStateOf("")
        private set
    var dealerCenter by mutableStateOf(DEALER_CENTERS.first())
    var contactPhone by mutableStateOf("")
    var notes by mutableStateOf("")

    var submitting by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            catalogRepo.getVehicle(vehicleId).onSuccess {
                vehicleName = "${it.brand?.name.orEmpty()} ${it.model}".trim()
            }
        }
    }

    fun book(scheduledAt: LocalDateTime?, onSuccess: () -> Unit) {
        val phone = contactPhone.trim()
        // Та же проверка, что и на сервере (TestDriveRequest): дата в будущем + формат телефона.
        // Возвращаем КОНКРЕТНОЕ сообщение, чтобы клиент не получал общий ответ «проверьте поле».
        val validationError = when {
            scheduledAt == null -> "Выберите дату и время тест-драйва"
            !scheduledAt.isAfter(LocalDateTime.now()) -> "Дата и время тест-драйва должны быть в будущем"
            phone.isBlank() -> "Укажите контактный телефон"
            !PHONE_REGEX.matches(phone) -> "Некорректный номер телефона (10–15 цифр, можно с «+»)"
            else -> null
        }
        if (validationError != null) { error = validationError; return }
        viewModelScope.launch {
            submitting = true; error = null
            testDriveRepo.book(
                TestDriveRequestDto(
                    vehicleId = vehicleId,
                    dealerCenter = dealerCenter,
                    scheduledAt = Format.toIso(scheduledAt!!),
                    contactPhone = phone,
                    notes = notes.ifBlank { null }
                )
            ).onSuccess { onSuccess() }.onFailure { error = it.message }
            submitting = false
        }
    }

    fun clearError() { error = null }

    companion object {
        val DEALER_CENTERS = listOf(
            "Автосалон на Ленина, 100",
            "Автосалон на Мира, 25",
            "Автосалон на Доватора, 50"
        )
        val TIME_SLOTS = listOf(10, 12, 14, 16, 18)

        /** Формат телефона — синхронизирован с серверной валидацией (@Pattern). */
        val PHONE_REGEX = Regex("^\\+?[0-9]{10,15}$")
    }
}
