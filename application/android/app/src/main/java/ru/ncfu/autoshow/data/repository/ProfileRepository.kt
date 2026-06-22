package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.UpdateProfileRequestDto
import ru.ncfu.autoshow.data.remote.dto.UserDto
import ru.ncfu.autoshow.data.session.SessionManager

/** Репозиторий профиля пользователя. */
class ProfileRepository(
    private val api: ApiService,
    private val session: SessionManager
) {
    suspend fun me(): Result<UserDto> =
        safeApiCall { api.me() }.onSuccess { session.updateUser(it) }

    suspend fun updateProfile(fullName: String?, phone: String?): Result<UserDto> =
        safeApiCall { api.updateProfile(UpdateProfileRequestDto(fullName, phone)) }
            .onSuccess { session.updateUser(it) }
}
