package ru.ncfu.autoshow.data.remote.dto

// ----- Запросы -----

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String?,
    val pdnConsent: Boolean
)

data class UpdateProfileRequestDto(
    val fullName: String?,
    val phone: String?
)

data class UpdateRoleRequestDto(
    val role: String
)

// ----- Ответы -----

data class AuthResponseDto(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: UserDto
)

data class UserDto(
    val id: Long,
    val fullName: String,
    val email: String,
    val phone: String?,
    val role: String,
    val loyaltyLevel: String,
    val active: Boolean,
    val createdAt: String?
)

data class MessageDto(
    val message: String
)
