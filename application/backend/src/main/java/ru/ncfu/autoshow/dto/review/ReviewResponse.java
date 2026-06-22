package ru.ncfu.autoshow.dto.review;

import java.time.LocalDateTime;

/** Представление отзыва. */
public record ReviewResponse(
        Long id,
        Long userId,
        String userName,
        Long vehicleId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
