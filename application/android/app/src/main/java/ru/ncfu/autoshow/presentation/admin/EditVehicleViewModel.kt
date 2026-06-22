package ru.ncfu.autoshow.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.data.remote.dto.BrandDto
import ru.ncfu.autoshow.data.remote.dto.VehicleRequestDto
import ru.ncfu.autoshow.data.repository.CatalogRepository

/** ViewModel формы создания/редактирования автомобиля. */
class EditVehicleViewModel(
    private val vehicleId: Long?,
    private val catalogRepo: CatalogRepository
) : ViewModel() {

    val isEdit = vehicleId != null

    var brands by mutableStateOf<List<BrandDto>>(emptyList())
        private set
    var brandId by mutableStateOf<Long?>(null)

    var model by mutableStateOf("")
    var vin by mutableStateOf("")
    var color by mutableStateOf("")
    var equipmentLevel by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf("")

    var yearText by mutableStateOf("2024")
    var priceText by mutableStateOf("")
    var mileageText by mutableStateOf("0")
    var powerText by mutableStateOf("")
    var volumeText by mutableStateOf("")
    var fuelText by mutableStateOf("")

    var bodyType by mutableStateOf("SEDAN")
    var engineType by mutableStateOf("PETROL")
    var transmission by mutableStateOf("AUTOMATIC")
    var driveType by mutableStateOf("FWD")
    var status by mutableStateOf("IN_STOCK")

    var loading by mutableStateOf(true)
        private set
    var saving by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            catalogRepo.getBrands().onSuccess { brands = it; if (brandId == null) brandId = it.firstOrNull()?.id }
            if (vehicleId != null) {
                catalogRepo.getVehicle(vehicleId).onSuccess { v ->
                    brandId = v.brand?.id
                    model = v.model
                    vin = v.vin
                    color = v.color
                    equipmentLevel = v.equipmentLevel.orEmpty()
                    description = v.description.orEmpty()
                    imageUrl = v.imageUrl.orEmpty()
                    yearText = v.year.toString()
                    priceText = v.price.toLong().toString()
                    mileageText = v.mileage.toString()
                    powerText = v.powerHp.toString()
                    volumeText = v.engineVolume?.toString().orEmpty()
                    fuelText = v.fuelConsumption?.toString().orEmpty()
                    bodyType = v.bodyType
                    engineType = v.engineType
                    transmission = v.transmission
                    driveType = v.driveType
                    status = v.status
                }
            }
            loading = false
        }
    }

    fun save(onSaved: () -> Unit) {
        val bId = brandId
        val year = yearText.toIntOrNull()
        val price = priceText.filter { it.isDigit() || it == '.' }.toDoubleOrNull()
        val power = powerText.toIntOrNull()
        val mileage = mileageText.toIntOrNull() ?: 0
        when {
            bId == null -> { error = "Выберите марку"; return }
            model.isBlank() -> { error = "Укажите модель"; return }
            vin.length != 17 -> { error = "VIN должен содержать 17 символов"; return }
            year == null -> { error = "Некорректный год"; return }
            price == null || price <= 0 -> { error = "Некорректная цена"; return }
            power == null || power <= 0 -> { error = "Некорректная мощность"; return }
            color.isBlank() -> { error = "Укажите цвет"; return }
        }
        val body = VehicleRequestDto(
            brandId = bId!!,
            model = model.trim(),
            year = year!!,
            vin = vin.trim().uppercase(),
            price = price!!,
            bodyType = bodyType,
            engineType = engineType,
            transmission = transmission,
            driveType = driveType,
            color = color.trim(),
            mileage = mileage,
            powerHp = power!!,
            engineVolume = volumeText.toDoubleOrNull(),
            fuelConsumption = fuelText.toDoubleOrNull(),
            equipmentLevel = equipmentLevel.ifBlank { null },
            description = description.ifBlank { null },
            imageUrl = imageUrl.ifBlank { null },
            status = status,
            images = null
        )
        viewModelScope.launch {
            saving = true; error = null
            val result = if (vehicleId != null) catalogRepo.updateVehicle(vehicleId, body)
            else catalogRepo.createVehicle(body)
            result.onSuccess { onSaved() }.onFailure { error = it.message }
            saving = false
        }
    }

    fun clearError() { error = null }
}
