package ru.ncfu.autoshow.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.data.repository.AuthRepository

/** ViewModel экранов входа и регистрации. */
class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var fullName by mutableStateOf("")
    var phone by mutableStateOf("")
    var consent by mutableStateOf(false)

    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            error = "Введите email и пароль"
            return
        }
        viewModelScope.launch {
            loading = true; error = null
            repo.login(email, password)
                .onSuccess { onSuccess() }
                .onFailure { error = it.message }
            loading = false
        }
    }

    fun register(onSuccess: () -> Unit) {
        when {
            fullName.isBlank() -> { error = "Укажите ФИО"; return }
            email.isBlank() -> { error = "Укажите email"; return }
            password.length < 6 -> { error = "Пароль должен содержать минимум 6 символов"; return }
            !consent -> { error = "Необходимо согласие на обработку персональных данных"; return }
        }
        viewModelScope.launch {
            loading = true; error = null
            repo.register(fullName, email, password, phone.ifBlank { null }, consent)
                .onSuccess { onSuccess() }
                .onFailure { error = it.message }
            loading = false
        }
    }

    fun clearError() { error = null }
}
