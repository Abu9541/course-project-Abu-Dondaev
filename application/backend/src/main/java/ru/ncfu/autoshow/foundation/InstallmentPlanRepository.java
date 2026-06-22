package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.InstallmentPlan;

import java.util.Optional;

/** Foundation: репозиторий планов рассрочки. */
@Repository
public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, Long> {

    Optional<InstallmentPlan> findByOrderId(Long orderId);
}
