package ru.ncfu.autoshow.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ncfu.autoshow.entity.enums.PaymentMethod;
import ru.ncfu.autoshow.entity.enums.PaymentStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сущность Payment (статусная модель платежа)")
class PaymentTest {

    private Payment newPayment() {
        return new Payment(new Order(), "pay_test_123", new BigDecimal("100000"), PaymentMethod.CARD);
    }

    @Test
    @DisplayName("Новый платёж создаётся в статусе PENDING")
    void newPaymentIsPending() {
        Payment p = newPayment();
        assertEquals(PaymentStatus.PENDING, p.getStatus());
        assertTrue(p.isPending());
        assertFalse(p.isSucceeded());
        assertNull(p.getMaskedCard());
    }

    @Test
    @DisplayName("succeed() переводит в SUCCEEDED и сохраняет маску карты")
    void succeedMarksSucceeded() {
        Payment p = newPayment();
        p.succeed("•••• 1111");
        assertEquals(PaymentStatus.SUCCEEDED, p.getStatus());
        assertTrue(p.isSucceeded());
        assertEquals("•••• 1111", p.getMaskedCard());
    }

    @Test
    @DisplayName("fail() переводит в FAILED")
    void failMarksFailed() {
        Payment p = newPayment();
        p.fail("•••• 0002");
        assertEquals(PaymentStatus.FAILED, p.getStatus());
        assertFalse(p.isSucceeded());
        assertEquals("•••• 0002", p.getMaskedCard());
    }
}
