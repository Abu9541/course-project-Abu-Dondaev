package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ncfu.autoshow.entity.enums.OrderStatus;
import ru.ncfu.autoshow.entity.enums.PaymentType;

import java.math.BigDecimal;

/**
 * Заказ на покупку автомобиля (полная оплата или рассрочка).
 * Управляет статусной моделью сделки.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private InstallmentPlan installmentPlan;

    // ----------------------------- бизнес-методы -----------------------------

    public boolean isInstallment() {
        return paymentType == PaymentType.INSTALLMENT;
    }

    public boolean isFinished() {
        return status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED;
    }

    /** Привязка плана рассрочки к заказу (двусторонняя связь). */
    public void attachInstallmentPlan(InstallmentPlan plan) {
        plan.setOrder(this);
        this.installmentPlan = plan;
    }

    public void confirm(User manager) {
        ensureNotFinished();
        this.status = OrderStatus.CONFIRMED;
        this.manager = manager;
    }

    public void markPaid() {
        ensureNotFinished();
        this.status = OrderStatus.PAID;
    }

    public void complete() {
        ensureNotFinished();
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя отменить завершённый заказ");
        }
        this.status = OrderStatus.CANCELLED;
    }

    private void ensureNotFinished() {
        if (isFinished()) {
            throw new IllegalStateException("Заказ уже завершён или отменён (статус: " + status + ")");
        }
    }
}
