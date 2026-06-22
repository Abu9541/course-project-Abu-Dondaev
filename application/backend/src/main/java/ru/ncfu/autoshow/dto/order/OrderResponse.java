package ru.ncfu.autoshow.dto.order;

import ru.ncfu.autoshow.entity.enums.OrderStatus;
import ru.ncfu.autoshow.entity.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Представление заказа на покупку. */
public record OrderResponse(
        Long id,
        Long userId,
        String userName,
        Long vehicleId,
        String vehicleName,
        String vehicleImageUrl,
        Long managerId,
        String managerName,
        PaymentType paymentType,
        OrderStatus status,
        BigDecimal totalPrice,
        InstallmentPlanResponse installmentPlan,
        LocalDateTime createdAt
) {
}
