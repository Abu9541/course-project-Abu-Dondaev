package ru.ncfu.autoshow.dto.notification;

import ru.ncfu.autoshow.entity.enums.NotificationType;

import java.time.LocalDateTime;

/** Представление уведомления. */
public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        boolean read,
        LocalDateTime createdAt
) {
}
