package ru.ncfu.autoshow.dto.order;

import java.math.BigDecimal;

/** Представление плана рассрочки. */
public record InstallmentPlanResponse(
        BigDecimal downPayment,
        Integer termMonths,
        BigDecimal interestRate,
        BigDecimal monthlyPayment,
        BigDecimal totalAmount,
        BigDecimal overpayment
) {
}
