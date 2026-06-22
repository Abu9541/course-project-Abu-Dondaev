package ru.ncfu.autoshow.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.VehicleSummaryDto
import ru.ncfu.autoshow.data.repository.CatalogRepository

/** ViewModel списка проданных автомобилей (отдельная подстраница для админа/персонала). */
class SoldVehiclesViewModel(private val repo: CatalogRepository) : ViewModel() {

    var state by mutableStateOf<UiState<List<VehicleSummaryDto>>>(UiState.Loading)
        private set

    fun load() {
        viewModelScope.launch {
            state = UiState.Loading
            repo.getSoldVehicles()
                .onSuccess { state = UiState.Success(it.content) }
                .onFailure { state = UiState.Error(it.message ?: "Не удалось загрузить список") }
        }
    }
}
