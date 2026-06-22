package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.Order;
import ru.ncfu.autoshow.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

/** Foundation: репозиторий заказов. */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    List<Order> findAllByOrderByCreatedAtDesc();

    boolean existsByVehicleIdAndStatusIn(Long vehicleId, java.util.Collection<OrderStatus> statuses);

    long countByStatus(OrderStatus status);

    /** Суммарная выручка по завершённым заказам (для дашборда администратора). */
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    BigDecimal totalRevenue();
}
