package ru.ncfu.autoshow.dto.common;

import java.time.LocalDateTime;
import java.util.Map;

/** Стандартизированное тело ответа об ошибке. */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ApiError of(int status, String error, String message, String path, Map<String, String> fieldErrors) {
        return new ApiError(LocalDateTime.now(), status, error, message, path, fieldErrors);
    }
}
