package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.Payment;
import ru.ncfu.autoshow.entity.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

/** Foundation: репозиторий платежей. */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);

    List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
