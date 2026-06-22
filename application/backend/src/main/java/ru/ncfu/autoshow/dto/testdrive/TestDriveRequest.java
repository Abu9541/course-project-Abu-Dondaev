package ru.ncfu.autoshow.dto.testdrive;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/** Запрос записи на тест-драйв. */
public record TestDriveRequest(
        @NotNull(message = "Автомобиль обязателен")
        Long vehicleId,

        @NotBlank(message = "Укажите дилерский центр")
        @Size(max = 120)
        String dealerCenter,

        @NotNull(message = "Укажите дату и время")
        @Future(message = "Дата тест-драйва должна быть в будущем")
        LocalDateTime scheduledAt,

        @NotBlank(message = "Контактный телефон обязателен")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный номер телефона")
        String contactPhone,

        @Size(max = 500)
        String notes
) {
}
