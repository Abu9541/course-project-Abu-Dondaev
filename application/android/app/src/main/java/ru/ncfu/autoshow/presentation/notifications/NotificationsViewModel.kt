package ru.ncfu.autoshow.presentation.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.NotificationDto
import ru.ncfu.autoshow.data.repository.NotificationRepository

class NotificationsViewModel(private val repo: NotificationRepository) : ViewModel() {

    var state by mutableStateOf<UiState<List<NotificationDto>>>(UiState.Loading)
        private set

    fun load() {
        viewModelScope.launch {
            state = UiState.Loading
            repo.getMine()
                .onSuccess { state = UiState.Success(it) }
                .onFailure { state = UiState.Error(it.message ?: "Не удалось загрузить уведомления") }
        }
    }

    fun markRead(id: Long) {
        viewModelScope.launch { repo.markRead(id).onSuccess { load() } }
    }

    fun markAllRead() {
        viewModelScope.launch { repo.markAllRead().onSuccess { load() } }
    }
}
