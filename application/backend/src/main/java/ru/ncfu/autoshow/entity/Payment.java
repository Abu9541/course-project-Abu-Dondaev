package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ncfu.autoshow.entity.enums.PaymentMethod;
import ru.ncfu.autoshow.entity.enums.PaymentStatus;

import java.math.BigDecimal;

/**
 * Платёж по заказу. Имитирует поведение платёжного шлюза: создаётся в статусе
 * PENDING, затем подтверждается (SUCCEEDED) или отклоняется (FAILED).
 * Полный номер карты НЕ хранится (только маскированный) — как и требует PCI DSS;
 * в реальной системе данные карты обрабатывал бы внешний платёжный провайдер.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "provider_payment_id", nullable = false, unique = true, length = 60)
    private String providerPaymentId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PaymentMethod method = PaymentMethod.CARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "masked_card", length = 25)
    private String maskedCard;

    public Payment(Order order, String providerPaymentId, BigDecimal amount, PaymentMethod method) {
        this.order = order;
        this.providerPaymentId = providerPaymentId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public boolean isPending()   { return status == PaymentStatus.PENDING; }
    public boolean isSucceeded() { return status == PaymentStatus.SUCCEEDED; }

    /** Успешное подтверждение оплаты. */
    public void succeed(String maskedCard) {
        this.status = PaymentStatus.SUCCEEDED;
        this.maskedCard = maskedCard;
    }

    /** Отклонение оплаты (например, банк отклонил операцию). */
    public void fail(String maskedCard) {
        this.status = PaymentStatus.FAILED;
        this.maskedCard = maskedCard;
    }
}
