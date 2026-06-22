package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.*
import ru.ncfu.autoshow.data.session.SessionManager

/** Репозиторий аутентификации: вход/регистрация и сохранение сессии. */
class AuthRepository(
    private val api: ApiService,
    private val session: SessionManager
) {
    suspend fun login(email: String, password: String): Result<AuthResponseDto> =
        safeApiCall { api.login(LoginRequestDto(email.trim(), password)) }
            .onSuccess { session.save(it) }

    suspend fun register(
        fullName: String, email: String, password: String, phone: String?, consent: Boolean
    ): Result<AuthResponseDto> =
        safeApiCall { api.register(RegisterRequestDto(fullName.trim(), email.trim(), password, phone, consent)) }
            .onSuccess { session.save(it) }

    suspend fun logout() = session.clear()
}
