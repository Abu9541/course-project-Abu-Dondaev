package ru.ncfu.autoshow.dto.user;

import ru.ncfu.autoshow.entity.enums.LoyaltyLevel;
import ru.ncfu.autoshow.entity.enums.Role;

import java.time.LocalDateTime;

/** Представление пользователя (без пароля). */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        Role role,
        LoyaltyLevel loyaltyLevel,
        boolean active,
        LocalDateTime createdAt
) {
}
