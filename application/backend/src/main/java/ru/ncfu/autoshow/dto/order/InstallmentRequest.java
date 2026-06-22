package ru.ncfu.autoshow.dto.order;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Параметры рассрочки внутри заказа. */
public record InstallmentRequest(
        @NotNull(message = "Укажите первоначальный взнос")
        @PositiveOrZero(message = "Первоначальный взнос не может быть отрицательным")
        BigDecimal downPayment,

        @NotNull(message = "Укажите срок рассрочки")
        @Min(value = 3, message = "Минимальный срок рассрочки — 3 месяца")
        @Max(value = 84, message = "Максимальный срок рассрочки — 84 месяца")
        Integer termMonths
) {
}
