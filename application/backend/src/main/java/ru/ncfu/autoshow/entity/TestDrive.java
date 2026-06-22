package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ncfu.autoshow.entity.enums.TestDriveStatus;

import java.time.LocalDateTime;

/**
 * Запись клиента на тест-драйв. Управляет своим жизненным циклом через
 * бизнес-методы (подтверждение, отмена, завершение, отклонение).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "test_drives")
public class TestDrive extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(name = "dealer_center", nullable = false, length = 120)
    private String dealerCenter;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TestDriveStatus status = TestDriveStatus.PENDING;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(length = 500)
    private String notes;

    // ----------------------------- бизнес-методы -----------------------------

    /** Запись можно изменить, пока она не завершена/не отменена. */
    public boolean isActive() {
        return status == TestDriveStatus.PENDING || status == TestDriveStatus.CONFIRMED;
    }

    public boolean isUpcoming() {
        return isActive() && scheduledAt != null && scheduledAt.isAfter(LocalDateTime.now());
    }

    public void confirm(User manager) {
        ensureActive();
        this.status = TestDriveStatus.CONFIRMED;
        this.manager = manager;
    }

    public void complete() {
        if (status != TestDriveStatus.CONFIRMED) {
            throw new IllegalStateException("Завершить можно только подтверждённую запись");
        }
        this.status = TestDriveStatus.COMPLETED;
    }

    public void cancel() {
        ensureActive();
        this.status = TestDriveStatus.CANCELLED;
    }

    public void reject(User manager) {
        ensureActive();
        this.status = TestDriveStatus.REJECTED;
        this.manager = manager;
    }

    public void reschedule(LocalDateTime newTime) {
        ensureActive();
        if (newTime == null || newTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Новое время тест-драйва должно быть в будущем");
        }
        this.scheduledAt = newTime;
        this.status = TestDriveStatus.PENDING;
    }

    private void ensureActive() {
        if (!isActive()) {
            throw new IllegalStateException("Действие недоступно для записи в статусе " + status);
        }
    }
}
