package ru.ncfu.autoshow.presentation.catalog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.BrandDto
import ru.ncfu.autoshow.data.remote.dto.VehicleSummaryDto
import ru.ncfu.autoshow.data.repository.CatalogRepository

/** ViewModel каталога: поиск, фильтрация, оффлайн-кэш. */
class CatalogViewModel(private val repo: CatalogRepository) : ViewModel() {

    var query by mutableStateOf("")
        private set
    var selectedBrandId by mutableStateOf<Long?>(null)
        private set
    var selectedBodyType by mutableStateOf<String?>(null)
        private set

    var state by mutableStateOf<UiState<List<VehicleSummaryDto>>>(UiState.Loading)
        private set
    var brands by mutableStateOf<List<BrandDto>>(emptyList())
        private set
    var offline by mutableStateOf(false)
        private set

    init {
        loadBrands()
        load()
    }

    fun onQueryChange(value: String) { query = value }

    fun applySearch() = load()

    fun selectBrand(id: Long?) {
        selectedBrandId = id
        load()
    }

    fun selectBodyType(type: String?) {
        selectedBodyType = type
        load()
    }

    /** Сброс всех фильтров и строки поиска. */
    fun clearFilters() {
        query = ""
        selectedBrandId = null
        selectedBodyType = null
        load()
    }

    fun load() {
        viewModelScope.launch {
            state = UiState.Loading
            offline = false
            repo.getVehicles(
                q = query.ifBlank { null },
                brandId = selectedBrandId,
                bodyType = selectedBodyType,
                status = null, minPrice = null, maxPrice = null,
                page = 0, size = 50, sortBy = "price", direction = "asc"
            ).onSuccess {
                state = UiState.Success(it.content)
            }.onFailure { e ->
                val cached = repo.getCachedVehicles()
                if (cached.isNotEmpty()) {
                    offline = true
                    state = UiState.Success(cached)
                } else {
                    state = UiState.Error(e.message ?: "Не удалось загрузить каталог")
                }
            }
        }
    }

    private fun loadBrands() {
        viewModelScope.launch {
            repo.getBrands().onSuccess { brands = it }
        }
    }
}
