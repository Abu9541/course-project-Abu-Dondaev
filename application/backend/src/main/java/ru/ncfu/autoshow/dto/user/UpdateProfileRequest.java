package ru.ncfu.autoshow.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Запрос обновления собственного профиля. */
public record UpdateProfileRequest(
        @Size(min = 2, max = 150, message = "ФИО должно содержать от 2 до 150 символов")
        String fullName,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный номер телефона")
        String phone
) {
}
