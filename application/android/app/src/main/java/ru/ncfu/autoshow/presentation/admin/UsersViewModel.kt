package ru.ncfu.autoshow.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.UserDto
import ru.ncfu.autoshow.data.repository.AdminRepository

class UsersViewModel(private val repo: AdminRepository) : ViewModel() {

    var state by mutableStateOf<UiState<List<UserDto>>>(UiState.Loading)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    fun load() {
        viewModelScope.launch {
            state = UiState.Loading
            repo.getUsers()
                .onSuccess { state = UiState.Success(it) }
                .onFailure { state = UiState.Error(it.message ?: "Не удалось загрузить пользователей") }
        }
    }

    fun updateRole(id: Long, role: String) {
        viewModelScope.launch {
            repo.updateRole(id, role)
                .onSuccess { message = "Роль обновлена"; load() }
                .onFailure { message = it.message }
        }
    }

    fun setActive(id: Long, active: Boolean) {
        viewModelScope.launch {
            repo.setStatus(id, active)
                .onSuccess { message = if (active) "Пользователь активирован" else "Пользователь заблокирован"; load() }
                .onFailure { message = it.message }
        }
    }

    fun consumeMessage() { message = null }
}
