package ru.ncfu.autoshow.presentation.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.VehicleSummaryDto
import ru.ncfu.autoshow.data.repository.FavoriteRepository

class FavoritesViewModel(private val repo: FavoriteRepository) : ViewModel() {

    var state by mutableStateOf<UiState<List<VehicleSummaryDto>>>(UiState.Loading)
        private set

    fun load() {
        viewModelScope.launch {
            state = UiState.Loading
            repo.getMine()
                .onSuccess { state = UiState.Success(it) }
                .onFailure { state = UiState.Error(it.message ?: "Не удалось загрузить избранное") }
        }
    }

    fun remove(vehicleId: Long) {
        viewModelScope.launch { repo.remove(vehicleId).onSuccess { load() } }
    }
}
