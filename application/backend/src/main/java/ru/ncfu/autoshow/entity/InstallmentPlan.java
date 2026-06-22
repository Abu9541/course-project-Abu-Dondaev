package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * План рассрочки (1:1 к заказу). Содержит бизнес-логику расчёта аннуитетного
 * ежемесячного платежа и итоговой суммы.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "installment_plans")
public class InstallmentPlan extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "down_payment", nullable = false, precision = 12, scale = 2)
    private BigDecimal downPayment;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "monthly_payment", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Фабричный метод: рассчитывает аннуитетный платёж и общую сумму рассрочки.
     *
     * @param price        полная стоимость автомобиля
     * @param downPayment  первоначальный взнос
     * @param termMonths   срок в месяцах
     * @param annualRate   годовая процентная ставка (%)
     */
    public static InstallmentPlan calculate(BigDecimal price, BigDecimal downPayment,
                                            int termMonths, BigDecimal annualRate) {
        BigDecimal principal = price.subtract(downPayment);
        if (principal.signum() <= 0) {
            throw new IllegalArgumentException("Сумма к рассрочке должна быть положительной");
        }

        BigDecimal monthly;
        if (annualRate.signum() == 0) {
            // Беспроцентная рассрочка
            monthly = principal.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        } else {
            double r = annualRate.doubleValue() / 100.0 / 12.0;
            double p = principal.doubleValue();
            double pow = Math.pow(1 + r, termMonths);
            double m = p * (r * pow) / (pow - 1);
            monthly = BigDecimal.valueOf(m).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal total = downPayment.add(monthly.multiply(BigDecimal.valueOf(termMonths)))
                .setScale(2, RoundingMode.HALF_UP);

        InstallmentPlan plan = new InstallmentPlan();
        plan.downPayment = downPayment.setScale(2, RoundingMode.HALF_UP);
        plan.termMonths = termMonths;
        plan.interestRate = annualRate;
        plan.monthlyPayment = monthly;
        plan.totalAmount = total;
        return plan;
    }

    /** Переплата за пользование рассрочкой. */
    public BigDecimal overpayment() {
        BigDecimal financed = totalAmount.subtract(downPayment);
        BigDecimal principal = order != null ? order.getTotalPrice().subtract(downPayment) : financed;
        return financed.subtract(principal).max(BigDecimal.ZERO);
    }
}
