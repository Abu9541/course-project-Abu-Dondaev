package ru.ncfu.autoshow.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Расчёт плана рассрочки (бизнес-логика сущности)")
class InstallmentPlanTest {

    @Test
    @DisplayName("Аннуитетный платёж рассчитывается корректно и больше нуля")
    void calculatesAnnuityPayment() {
        InstallmentPlan plan = InstallmentPlan.calculate(
                new BigDecimal("3000000"), new BigDecimal("600000"), 24, new BigDecimal("12.0"));

        assertEquals(0, plan.getDownPayment().compareTo(new BigDecimal("600000.00")));
        assertEquals(24, plan.getTermMonths());
        assertTrue(plan.getMonthlyPayment().signum() > 0);
        // Итог = взнос + платежи * срок, и больше тела кредита из-за процентов
        assertTrue(plan.getTotalAmount().compareTo(new BigDecimal("3000000")) > 0);
    }

    @Test
    @DisplayName("Беспроцентная рассрочка: сумма платежей равна телу кредита")
    void zeroRateMeansNoOverpayment() {
        InstallmentPlan plan = InstallmentPlan.calculate(
                new BigDecimal("2400000"), new BigDecimal("400000"), 20, BigDecimal.ZERO);

        // (2 400 000 - 400 000) / 20 = 100 000
        assertEquals(0, plan.getMonthlyPayment().compareTo(new BigDecimal("100000.00")));
        assertEquals(0, plan.getTotalAmount().compareTo(new BigDecimal("2400000.00")));
    }

    @Test
    @DisplayName("Первоначальный взнос не меньше цены приводит к исключению")
    void downPaymentNotLessThanPriceThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                InstallmentPlan.calculate(new BigDecimal("1000000"), new BigDecimal("1000000"), 12, new BigDecimal("10")));
    }
}
