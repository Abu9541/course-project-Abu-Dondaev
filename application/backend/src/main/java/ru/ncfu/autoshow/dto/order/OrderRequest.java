package ru.ncfu.autoshow.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.ncfu.autoshow.entity.enums.PaymentType;

/**
 * Запрос на покупку автомобиля. Для типа INSTALLMENT обязателен блок
 * {@code installment} (проверяется в сервисном слое).
 */
public record OrderRequest(
        @NotNull(message = "Автомобиль обязателен")
        Long vehicleId,

        @NotNull(message = "Укажите тип оплаты")
        PaymentType paymentType,

        @Valid
        InstallmentRequest installment
) {
}
