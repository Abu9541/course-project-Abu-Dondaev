package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.payment.ConfirmPaymentRequest;
import ru.ncfu.autoshow.dto.payment.PaymentResponse;

/** Mediator: оплата заказов (имитация платёжного шлюза). */
public interface PaymentService {

    /**
     * Создать платёж для заказа клиента (или вернуть уже существующий ожидающий).
     * Сумма рассчитывается на сервере (полная цена или первоначальный взнос по рассрочке).
     */
    PaymentResponse createForOrder(Long orderId, Long clientId);

    /**
     * Подтвердить платёж картой. При успехе заказ переводится в статус «оплачен»,
     * при отклонении банка платёж получает статус FAILED, заказ остаётся неоплаченным.
     */
    PaymentResponse confirm(String providerPaymentId, Long clientId, ConfirmPaymentRequest request);
}
