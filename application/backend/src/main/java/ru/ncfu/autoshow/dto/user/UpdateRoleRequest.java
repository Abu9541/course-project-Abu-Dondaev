package ru.ncfu.autoshow.dto.user;

import jakarta.validation.constraints.NotNull;
import ru.ncfu.autoshow.entity.enums.Role;

/** Запрос изменения роли пользователя (доступно администратору). */
public record UpdateRoleRequest(
        @NotNull(message = "Роль обязательна")
        Role role
) {
}
