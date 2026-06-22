package ru.ncfu.autoshow.dto.auth;

import ru.ncfu.autoshow.dto.user.UserResponse;

/** Ответ при успешной аутентификации/регистрации. */
public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
    public static AuthResponse of(String token, long expiresIn, UserResponse user) {
        return new AuthResponse(token, "Bearer", expiresIn, user);
    }
}
