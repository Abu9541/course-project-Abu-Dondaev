package ru.ncfu.autoshow.mapper;

import org.springframework.stereotype.Component;
import ru.ncfu.autoshow.dto.payment.PaymentResponse;
import ru.ncfu.autoshow.entity.Payment;

/** Data Mapper для платежей. */
@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getProviderPaymentId(),
                p.getOrder().getId(),
                p.getAmount(),
                p.getMethod(),
                p.getStatus(),
                p.getMaskedCard(),
                p.getCreatedAt()
        );
    }
}
