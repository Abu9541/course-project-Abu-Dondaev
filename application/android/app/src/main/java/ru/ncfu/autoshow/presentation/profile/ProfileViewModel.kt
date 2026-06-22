package ru.ncfu.autoshow.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.UserDto
import ru.ncfu.autoshow.data.repository.AuthRepository
import ru.ncfu.autoshow.data.repository.ProfileRepository

class ProfileViewModel(
    private val profileRepo: ProfileRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    var state by mutableStateOf<UiState<UserDto>>(UiState.Loading)
        private set
    var editing by mutableStateOf(false)
        private set
    var fullName by mutableStateOf("")
    var phone by mutableStateOf("")
    var saving by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    fun load() {
        viewModelScope.launch {
            state = UiState.Loading
            profileRepo.me()
                .onSuccess {
                    state = UiState.Success(it)
                    fullName = it.fullName
                    phone = it.phone.orEmpty()
                }
                .onFailure { state = UiState.Error(it.message ?: "Не удалось загрузить профиль") }
        }
    }

    fun startEdit() { editing = true }

    fun cancelEdit() {
        editing = false
        (state as? UiState.Success)?.data?.let {
            fullName = it.fullName
            phone = it.phone.orEmpty()
        }
    }

    fun save() {
        viewModelScope.launch {
            saving = true
            profileRepo.updateProfile(fullName, phone.ifBlank { null })
                .onSuccess { state = UiState.Success(it); editing = false; message = "Профиль обновлён" }
                .onFailure { message = it.message }
            saving = false
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch { authRepo.logout(); onDone() }
    }

    fun consumeMessage() { message = null }
}
