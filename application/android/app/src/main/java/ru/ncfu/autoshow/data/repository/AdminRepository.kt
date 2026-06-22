package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.UpdateRoleRequestDto
import ru.ncfu.autoshow.data.remote.dto.UserDto

/** Репозиторий администрирования пользователей. */
class AdminRepository(private val api: ApiService) {

    suspend fun getUsers(): Result<List<UserDto>> = safeApiCall { api.users() }

    suspend fun updateRole(id: Long, role: String): Result<UserDto> =
        safeApiCall { api.updateUserRole(id, UpdateRoleRequestDto(role)) }

    suspend fun setStatus(id: Long, active: Boolean): Result<UserDto> =
        safeApiCall { api.setUserStatus(id, active) }
}
