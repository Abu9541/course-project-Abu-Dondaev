package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.order.InstallmentCalcRequest;
import ru.ncfu.autoshow.dto.order.InstallmentPlanResponse;
import ru.ncfu.autoshow.dto.order.OrderRequest;
import ru.ncfu.autoshow.dto.order.OrderResponse;

import java.util.List;

/** Mediator: покупка автомобилей (полная оплата и рассрочка). */
public interface OrderService {

    /** Предварительный расчёт рассрочки без создания заказа (калькулятор). */
    InstallmentPlanResponse calculate(InstallmentCalcRequest request);

    OrderResponse buy(Long clientId, OrderRequest request);

    List<OrderResponse> getMine(Long userId);

    List<OrderResponse> getAll();

    OrderResponse confirm(Long id, Long managerId);

    OrderResponse markPaid(Long id);

    OrderResponse complete(Long id, Long managerId);

    OrderResponse cancel(Long id, Long actorId, boolean staff);
}
