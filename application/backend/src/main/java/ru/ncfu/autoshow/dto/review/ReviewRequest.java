package ru.ncfu.autoshow.dto.review;

import jakarta.validation.constraints.*;

/** Запрос создания отзыва об автомобиле. */
public record ReviewRequest(
        @NotNull(message = "Укажите оценку")
        @Min(value = 1, message = "Минимальная оценка — 1")
        @Max(value = 5, message = "Максимальная оценка — 5")
        Integer rating,

        @Size(max = 1000, message = "Комментарий не должен превышать 1000 символов")
        String comment
) {
}
