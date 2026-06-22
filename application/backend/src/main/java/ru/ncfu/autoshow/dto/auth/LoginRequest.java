package ru.ncfu.autoshow.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Запрос аутентификации. */
public record LoginRequest(
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Пароль обязателен")
        String password
) {
}
