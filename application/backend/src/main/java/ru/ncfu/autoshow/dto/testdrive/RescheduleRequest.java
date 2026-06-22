package ru.ncfu.autoshow.dto.testdrive;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** Запрос переноса тест-драйва на новое время. */
public record RescheduleRequest(
        @NotNull(message = "Укажите новую дату и время")
        @Future(message = "Новое время должно быть в будущем")
        LocalDateTime scheduledAt
) {
}
