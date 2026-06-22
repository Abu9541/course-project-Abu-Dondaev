package ru.ncfu.autoshow.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.DashboardDto
import ru.ncfu.autoshow.data.repository.DashboardRepository

class DashboardViewModel(private val repo: DashboardRepository) : ViewModel() {

    var state by mutableStateOf<UiState<DashboardDto>>(UiState.Loading)
        private set

    fun load() {
        viewModelScope.launch {
            state = UiState.Loading
            repo.getStats()
                .onSuccess { state = UiState.Success(it) }
                .onFailure { state = UiState.Error(it.message ?: "Не удалось загрузить статистику") }
        }
    }
}
