package ru.ncfu.autoshow.dto.payment;

import jakarta.validation.constraints.*;

/**
 * Запрос подтверждения оплаты картой (имитация ввода реквизитов).
 * CVC намеренно не передаётся и нигде не хранится.
 */
public record ConfirmPaymentRequest(
        @NotBlank(message = "Укажите номер карты")
        @Pattern(regexp = "\\d{13,19}", message = "Номер карты должен содержать 13–19 цифр")
        String cardNumber,

        @NotNull(message = "Укажите месяц действия")
        @Min(value = 1, message = "Некорректный месяц")
        @Max(value = 12, message = "Некорректный месяц")
        Integer expiryMonth,

        @NotNull(message = "Укажите год действия")
        @Min(value = 2000, message = "Некорректный год")
        @Max(value = 2100, message = "Некорректный год")
        Integer expiryYear,

        @NotBlank(message = "Укажите держателя карты")
        @Size(max = 100)
        String cardHolder
) {
}
