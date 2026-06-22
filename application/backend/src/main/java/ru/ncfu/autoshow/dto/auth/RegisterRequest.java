package ru.ncfu.autoshow.dto.auth;

import jakarta.validation.constraints.*;

/** Запрос регистрации нового клиента. */
public record RegisterRequest(
        @NotBlank(message = "Укажите ФИО")
        @Size(min = 2, max = 150, message = "ФИО должно содержать от 2 до 150 символов")
        String fullName,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        @Size(max = 150)
        String email,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, max = 72, message = "Пароль должен содержать от 6 до 72 символов")
        String password,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный номер телефона")
        String phone,

        @AssertTrue(message = "Необходимо согласие на обработку персональных данных")
        boolean pdnConsent
) {
}
