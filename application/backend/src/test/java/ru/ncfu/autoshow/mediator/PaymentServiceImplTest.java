package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.autoshow.dto.payment.ConfirmPaymentRequest;
import ru.ncfu.autoshow.dto.payment.PaymentResponse;
import ru.ncfu.autoshow.entity.Brand;
import ru.ncfu.autoshow.entity.Order;
import ru.ncfu.autoshow.entity.Payment;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.*;
import ru.ncfu.autoshow.exception.AccessForbiddenException;
import ru.ncfu.autoshow.exception.BusinessRuleException;
import ru.ncfu.autoshow.foundation.OrderRepository;
import ru.ncfu.autoshow.foundation.PaymentRepository;
import ru.ncfu.autoshow.mapper.PaymentMapper;
import ru.ncfu.autoshow.mediator.impl.PaymentServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис оплаты (имитация платёжного шлюза)")
class PaymentServiceImplTest {

    @Mock PaymentRepository paymentRepository;
    @Mock OrderRepository orderRepository;
    @Mock NotificationService notificationService;

    PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PaymentServiceImpl(paymentRepository, orderRepository, notificationService, new PaymentMapper());
    }

    private Order order(OrderStatus status, long ownerId) {
        User user = new User();
        user.setId(ownerId);
        user.setFullName("Магомед Дудаев");
        user.setRole(Role.CLIENT);
        Vehicle v = new Vehicle();
        v.setBrand(new Brand("Toyota", "Япония"));
        v.setModel("Camry");
        v.setYear(2024);
        Order o = new Order();
        o.setId(10L);
        o.setUser(user);
        o.setVehicle(v);
        o.setPaymentType(PaymentType.FULL);
        o.setStatus(status);
        o.setTotalPrice(new BigDecimal("3590000"));
        return o;
    }

    private ConfirmPaymentRequest card(String number) {
        return new ConfirmPaymentRequest(number, 12, 2030, "IVAN IVANOV");
    }

    // --------------------- createForOrder ---------------------

    @Test
    @DisplayName("Создание платежа: сумма равна полной цене, статус PENDING")
    void createForOrderFull() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order(OrderStatus.PENDING, 4L)));
        when(paymentRepository.findByOrderIdAndStatus(10L, PaymentStatus.PENDING)).thenReturn(List.of());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse r = service.createForOrder(10L, 4L);

        assertEquals(PaymentStatus.PENDING, r.status());
        assertEquals(PaymentMethod.CARD, r.method());
        assertEquals(0, r.amount().compareTo(new BigDecimal("3590000")));
    }

    @Test
    @DisplayName("Создание платежа для уже оплаченного заказа отклоняется")
    void createForOrderAlreadyPaidThrows() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order(OrderStatus.PAID, 4L)));
        assertThrows(BusinessRuleException.class, () -> service.createForOrder(10L, 4L));
    }

    @Test
    @DisplayName("Создание платежа для чужого заказа запрещено (403)")
    void createForOrderForeignForbidden() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order(OrderStatus.PENDING, 4L)));
        assertThrows(AccessForbiddenException.class, () -> service.createForOrder(10L, 5L));
    }

    // --------------------- confirm ---------------------

    @Test
    @DisplayName("Успешная оплата переводит заказ в PAID")
    void confirmSuccessMarksOrderPaid() {
        Order o = order(OrderStatus.CONFIRMED, 4L);
        Payment payment = new Payment(o, "pay_ok", o.getTotalPrice(), PaymentMethod.CARD);
        when(paymentRepository.findByProviderPaymentId("pay_ok")).thenReturn(Optional.of(payment));

        PaymentResponse r = service.confirm("pay_ok", 4L, card("4111111111111111"));

        assertEquals(PaymentStatus.SUCCEEDED, r.status());
        assertTrue(payment.isSucceeded());
        assertEquals(OrderStatus.PAID, o.getStatus());
    }

    @Test
    @DisplayName("Тестовая карта-отказ: платёж FAILED, заказ не оплачивается")
    void confirmDeclineCard() {
        Order o = order(OrderStatus.PENDING, 4L);
        Payment payment = new Payment(o, "pay_decline", o.getTotalPrice(), PaymentMethod.CARD);
        when(paymentRepository.findByProviderPaymentId("pay_decline")).thenReturn(Optional.of(payment));

        PaymentResponse r = service.confirm("pay_decline", 4L, card("4000000000000002"));

        assertEquals(PaymentStatus.FAILED, r.status());
        assertNotEquals(OrderStatus.PAID, o.getStatus());
    }

    @Test
    @DisplayName("Некорректный номер карты (Луна) отклоняется")
    void confirmInvalidCardThrows() {
        Order o = order(OrderStatus.PENDING, 4L);
        Payment payment = new Payment(o, "pay_bad", o.getTotalPrice(), PaymentMethod.CARD);
        when(paymentRepository.findByProviderPaymentId("pay_bad")).thenReturn(Optional.of(payment));

        assertThrows(BusinessRuleException.class, () -> service.confirm("pay_bad", 4L, card("4111111111111112")));
    }
}
