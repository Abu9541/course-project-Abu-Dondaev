package ru.ncfu.autoshow.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ncfu.autoshow.entity.enums.OrderStatus;
import ru.ncfu.autoshow.entity.enums.PaymentType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Статусная модель заказа")
class OrderTest {

    private Order pendingOrder() {
        Order o = new Order();
        o.setPaymentType(PaymentType.FULL);
        o.setStatus(OrderStatus.PENDING);
        o.setTotalPrice(new BigDecimal("3590000"));
        return o;
    }

    @Test
    @DisplayName("Заказ проходит цикл подтверждение → оплата → завершение")
    void lifecycle() {
        Order o = pendingOrder();
        User manager = new User();
        o.confirm(manager);
        assertEquals(OrderStatus.CONFIRMED, o.getStatus());
        o.markPaid();
        assertEquals(OrderStatus.PAID, o.getStatus());
        o.complete();
        assertEquals(OrderStatus.COMPLETED, o.getStatus());
        assertTrue(o.isFinished());
    }

    @Test
    @DisplayName("Завершённый заказ нельзя отменить")
    void cannotCancelCompleted() {
        Order o = pendingOrder();
        o.complete();
        assertThrows(IllegalStateException.class, o::cancel);
    }
}
