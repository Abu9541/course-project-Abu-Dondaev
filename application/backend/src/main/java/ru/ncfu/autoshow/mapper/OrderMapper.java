package ru.ncfu.autoshow.mapper;

import org.springframework.stereotype.Component;
import ru.ncfu.autoshow.dto.order.InstallmentPlanResponse;
import ru.ncfu.autoshow.dto.order.OrderResponse;
import ru.ncfu.autoshow.entity.InstallmentPlan;
import ru.ncfu.autoshow.entity.Order;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;

/** Data Mapper для заказов и планов рассрочки. */
@Component
public class OrderMapper {

    public OrderResponse toResponse(Order o) {
        User user = o.getUser();
        Vehicle vehicle = o.getVehicle();
        User manager = o.getManager();
        return new OrderResponse(
                o.getId(),
                user != null ? user.getId() : null,
                user != null ? user.getFullName() : null,
                vehicle != null ? vehicle.getId() : null,
                vehicle != null ? vehicle.fullName() : null,
                vehicle != null ? vehicle.getImageUrl() : null,
                manager != null ? manager.getId() : null,
                manager != null ? manager.getFullName() : null,
                o.getPaymentType(),
                o.getStatus(),
                o.getTotalPrice(),
                o.getInstallmentPlan() != null ? toInstallmentResponse(o.getInstallmentPlan()) : null,
                o.getCreatedAt()
        );
    }

    public InstallmentPlanResponse toInstallmentResponse(InstallmentPlan p) {
        return new InstallmentPlanResponse(
                p.getDownPayment(),
                p.getTermMonths(),
                p.getInterestRate(),
                p.getMonthlyPayment(),
                p.getTotalAmount(),
                p.overpayment()
        );
    }
}
