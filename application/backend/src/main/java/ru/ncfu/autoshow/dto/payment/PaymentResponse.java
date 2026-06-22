package ru.ncfu.autoshow.dto.payment;

import ru.ncfu.autoshow.entity.enums.PaymentMethod;
import ru.ncfu.autoshow.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Представление платежа по заказу. */
public record PaymentResponse(
        Long id,
        String providerPaymentId,
        Long orderId,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        String maskedCard,
        LocalDateTime createdAt
) {
}
