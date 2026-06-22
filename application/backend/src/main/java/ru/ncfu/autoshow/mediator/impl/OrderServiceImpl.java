package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.order.*;
import ru.ncfu.autoshow.entity.InstallmentPlan;
import ru.ncfu.autoshow.entity.Order;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.NotificationType;
import ru.ncfu.autoshow.entity.enums.OrderStatus;
import ru.ncfu.autoshow.entity.enums.PaymentType;
import ru.ncfu.autoshow.exception.AccessForbiddenException;
import ru.ncfu.autoshow.exception.BusinessRuleException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.FavoriteRepository;
import ru.ncfu.autoshow.foundation.OrderRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.OrderMapper;
import ru.ncfu.autoshow.mediator.NotificationService;
import ru.ncfu.autoshow.mediator.OrderService;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final List<OrderStatus> ACTIVE_ORDER_STATUSES =
            List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PAID);

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final NotificationService notificationService;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, VehicleRepository vehicleRepository,
                            UserRepository userRepository, FavoriteRepository favoriteRepository,
                            NotificationService notificationService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.notificationService = notificationService;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public InstallmentPlanResponse calculate(InstallmentCalcRequest request) {
        Vehicle vehicle = requireVehicle(request.vehicleId());
        BigDecimal rate = determineRate(request.termMonths());
        InstallmentPlan plan = buildPlan(vehicle.getPrice(), request.downPayment(), request.termMonths(), rate);
        BigDecimal overpayment = plan.getTotalAmount().subtract(vehicle.getPrice()).max(BigDecimal.ZERO);
        return new InstallmentPlanResponse(plan.getDownPayment(), plan.getTermMonths(), plan.getInterestRate(),
                plan.getMonthlyPayment(), plan.getTotalAmount(), overpayment);
    }

    @Override
    public OrderResponse buy(Long clientId, OrderRequest request) {
        User client = requireUser(clientId);
        Vehicle vehicle = requireVehicle(request.vehicleId());

        if (!vehicle.isAvailable()) {
            throw new BusinessRuleException("Автомобиль недоступен для покупки (статус: " + vehicle.getStatus() + ")");
        }
        if (orderRepository.existsByVehicleIdAndStatusIn(vehicle.getId(), ACTIVE_ORDER_STATUSES)) {
            throw new BusinessRuleException("По этому автомобилю уже оформлен активный заказ");
        }

        Order order = new Order();
        order.setUser(client);
        order.setVehicle(vehicle);
        order.setPaymentType(request.paymentType());
        order.setTotalPrice(vehicle.getPrice());
        order.setStatus(OrderStatus.PENDING);

        if (request.paymentType() == PaymentType.INSTALLMENT) {
            InstallmentRequest ir = request.installment();
            if (ir == null) {
                throw new BusinessRuleException("Для покупки в рассрочку укажите параметры рассрочки");
            }
            BigDecimal rate = determineRate(ir.termMonths());
            InstallmentPlan plan = buildPlan(vehicle.getPrice(), ir.downPayment(), ir.termMonths(), rate);
            order.attachInstallmentPlan(plan);
        }

        // Резервирование автомобиля (бизнес-метод сущности проверяет доступность)
        vehicle.reserve();

        Order saved = orderRepository.save(order);
        String paymentInfo = order.isInstallment()
                ? "в рассрочку на " + order.getInstallmentPlan().getTermMonths() + " мес."
                : "с полной оплатой";
        notificationService.notify(client, "Заказ оформлен",
                "Ваш заказ на «" + vehicle.fullName() + "» (" + paymentInfo + ") принят в обработку.",
                order.isInstallment() ? NotificationType.INSTALLMENT : NotificationType.ORDER);
        notificationService.notifyManagers("Новый заказ",
                "Клиент " + client.getFullName() + " оформил заказ на «" + vehicle.fullName()
                        + "» (" + paymentInfo + ").",
                order.isInstallment() ? NotificationType.INSTALLMENT : NotificationType.ORDER);
        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMine(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(orderMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(orderMapper::toResponse).toList();
    }

    @Override
    public OrderResponse confirm(Long id, Long managerId) {
        Order order = requireOrder(id);
        order.confirm(requireUser(managerId));
        notificationService.notify(order.getUser(), "Заказ подтверждён",
                "Ваш заказ на «" + order.getVehicle().fullName() + "» подтверждён менеджером.",
                NotificationType.ORDER);
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse markPaid(Long id) {
        Order order = requireOrder(id);
        order.markPaid();
        notificationService.notify(order.getUser(), "Оплата получена",
                "Оплата по заказу на «" + order.getVehicle().fullName() + "» получена.",
                NotificationType.ORDER);
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse complete(Long id, Long managerId) {
        Order order = requireOrder(id);
        if (order.getManager() == null) {
            order.setManager(requireUser(managerId));
        }
        order.complete();
        order.getVehicle().markSold();
        // Проданный автомобиль убираем из избранного у всех пользователей.
        favoriteRepository.deleteByVehicleId(order.getVehicle().getId());
        notificationService.notify(order.getUser(), "Сделка завершена",
                "Поздравляем с покупкой «" + order.getVehicle().fullName() + "»!",
                NotificationType.ORDER);
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse cancel(Long id, Long actorId, boolean staff) {
        Order order = requireOrder(id);
        if (!staff && !order.getUser().getId().equals(actorId)) {
            throw new AccessForbiddenException("Заказ принадлежит другому пользователю");
        }
        order.cancel();
        order.getVehicle().returnToStock();
        notificationService.notify(order.getUser(), "Заказ отменён",
                "Заказ на «" + order.getVehicle().fullName() + "» отменён. Автомобиль снова доступен.",
                NotificationType.ORDER);
        // Если отменил клиент — уведомляем персонал, чтобы менеджеры видели отмену.
        if (!staff) {
            notificationService.notifyManagers("Клиент отменил заказ",
                    "Клиент " + order.getUser().getFullName() + " отменил заказ на «"
                            + order.getVehicle().fullName() + "».",
                    NotificationType.ORDER);
        }
        return orderMapper.toResponse(order);
    }

    // ----------------------------- helpers -----------------------------

    /** Построение плана рассрочки с проверкой бизнес-правила «взнос < цены». */
    private InstallmentPlan buildPlan(BigDecimal price, BigDecimal downPayment, int termMonths, BigDecimal rate) {
        if (downPayment.compareTo(price) >= 0) {
            throw new BusinessRuleException("Первоначальный взнос должен быть меньше стоимости автомобиля");
        }
        if (downPayment.signum() < 0) {
            throw new BusinessRuleException("Первоначальный взнос не может быть отрицательным");
        }
        return InstallmentPlan.calculate(price, downPayment, termMonths, rate);
    }

    /** Процентная ставка по сроку рассрочки (бизнес-политика автосалона). */
    private BigDecimal determineRate(int termMonths) {
        if (termMonths <= 12) return new BigDecimal("9.90");
        if (termMonths <= 36) return new BigDecimal("12.50");
        if (termMonths <= 60) return new BigDecimal("14.90");
        return new BigDecimal("16.90");
    }

    private Order requireOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ", id));
    }

    private Vehicle requireVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автомобиль", id));
    }

    private User requireUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", id));
    }
}
