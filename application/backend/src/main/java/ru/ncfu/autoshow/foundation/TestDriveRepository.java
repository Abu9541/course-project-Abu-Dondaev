package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.TestDrive;
import ru.ncfu.autoshow.entity.enums.TestDriveStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/** Foundation: репозиторий записей на тест-драйв. */
@Repository
public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {

    List<TestDrive> findByUserIdOrderByScheduledAtDesc(Long userId);

    List<TestDrive> findByStatusOrderByScheduledAtAsc(TestDriveStatus status);

    List<TestDrive> findAllByOrderByScheduledAtDesc();

    /** Сортировка по времени подачи заявки (для страницы обработки заявок персоналом). */
    List<TestDrive> findAllByOrderByCreatedAtDesc();

    /** Проверка пересечения слота тест-драйва для одного автомобиля. */
    boolean existsByVehicleIdAndScheduledAtBetweenAndStatusIn(
            Long vehicleId, LocalDateTime from, LocalDateTime to, Collection<TestDriveStatus> statuses);

    long countByStatus(TestDriveStatus status);
}
