package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.payment.ConfirmPaymentRequest;
import ru.ncfu.autoshow.dto.payment.PaymentResponse;
import ru.ncfu.autoshow.entity.Order;
import ru.ncfu.autoshow.entity.Payment;
import ru.ncfu.autoshow.entity.enums.NotificationType;
import ru.ncfu.autoshow.entity.enums.OrderStatus;
import ru.ncfu.autoshow.entity.enums.PaymentMethod;
import ru.ncfu.autoshow.entity.enums.PaymentStatus;
import ru.ncfu.autoshow.exception.AccessForbiddenException;
import ru.ncfu.autoshow.exception.BusinessRuleException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.OrderRepository;
import ru.ncfu.autoshow.foundation.PaymentRepository;
import ru.ncfu.autoshow.mapper.PaymentMapper;
import ru.ncfu.autoshow.mediator.NotificationService;
import ru.ncfu.autoshow.mediator.PaymentService;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Имитация платёжного шлюза. Создаёт платёж (PENDING), затем при подтверждении
 * картой проводит его (SUCCEEDED) и переводит заказ в «оплачен», либо отклоняет (FAILED).
 * Реальные деньги не списываются; данные карты не сохраняются (только маскированный номер).
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    /** Тестовая карта, которую «банк» всегда отклоняет (для демонстрации обработки отказа). */
    private static final String DECLINE_TEST_CARD = "4000000000000002";

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository,
                              NotificationService notificationService, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentResponse createForOrder(Long orderId, Long clientId) {
        Order order = requireOwnedOrder(orderId, clientId);
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessRuleException("Этот заказ уже оплачен");
        }
        if (order.isFinished()) {
            throw new BusinessRuleException("Заказ отменён — оплата невозможна");
        }
        // Идемпотентность: повторный заход на оплату не плодит дубликаты — возвращаем ожидающий платёж.
        Payment pending = paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.PENDING)
                .stream().findFirst().orElse(null);
        if (pending != null) {
            return paymentMapper.toResponse(pending);
        }
        Payment payment = new Payment(order, generatePaymentId(), amountFor(order), PaymentMethod.CARD);
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponse confirm(String providerPaymentId, Long clientId, ConfirmPaymentRequest request) {
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Платёж", providerPaymentId));
        if (!payment.getOrder().getUser().getId().equals(clientId)) {
            throw new AccessForbiddenException("Платёж принадлежит другому пользователю");
        }
        // Идемпотентность: повторное подтверждение успешного платежа не списывает повторно.
        if (payment.isSucceeded()) {
            return paymentMapper.toResponse(payment);
        }
        if (!payment.isPending()) {
            throw new BusinessRuleException("Платёж уже обработан");
        }

        String digits = request.cardNumber().replaceAll("\\s", "");
        validateCard(digits, request.expiryMonth(), request.expiryYear());
        String masked = "•••• " + digits.substring(digits.length() - 4);

        // Имитация ответа банка: специальная тестовая карта отклоняется.
        if (digits.equals(DECLINE_TEST_CARD)) {
            payment.fail(masked);
            return paymentMapper.toResponse(payment);
        }

        Order order = payment.getOrder();
        payment.succeed(masked);
        order.markPaid();
        notificationService.notify(order.getUser(), "Оплата прошла успешно",
                "Оплата заказа на «" + order.getVehicle().fullName() + "» на сумму "
                        + payment.getAmount() + " ₽ получена.",
                NotificationType.ORDER);
        notificationService.notifyManagers("Заказ оплачен",
                "Клиент " + order.getUser().getFullName() + " оплатил заказ на «"
                        + order.getVehicle().fullName() + "».",
                NotificationType.ORDER);
        return paymentMapper.toResponse(payment);
    }

    // ----------------------------- helpers -----------------------------

    /** Сумма к оплате: полная цена либо первоначальный взнос по рассрочке (считается на сервере). */
    private BigDecimal amountFor(Order order) {
        if (order.isInstallment() && order.getInstallmentPlan() != null) {
            return order.getInstallmentPlan().getDownPayment();
        }
        return order.getTotalPrice();
    }

    private void validateCard(String digits, int month, int year) {
        if (!luhnValid(digits)) {
            throw new BusinessRuleException("Некорректный номер карты");
        }
        if (YearMonth.of(year, month).isBefore(YearMonth.now())) {
            throw new BusinessRuleException("Срок действия карты истёк");
        }
    }

    /** Проверка номера карты по алгоритму Луна. */
    private boolean luhnValid(String digits) {
        int sum = 0;
        boolean alt = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (alt) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            alt = !alt;
        }
        return sum % 10 == 0;
    }

    private String generatePaymentId() {
        return "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }

    private Order requireOwnedOrder(Long orderId, Long clientId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ", orderId));
        if (!order.getUser().getId().equals(clientId)) {
            throw new AccessForbiddenException("Заказ принадлежит другому пользователю");
        }
        return order;
    }
}
