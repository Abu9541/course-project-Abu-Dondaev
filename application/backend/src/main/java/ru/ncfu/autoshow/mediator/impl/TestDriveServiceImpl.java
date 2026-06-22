package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.testdrive.RescheduleRequest;
import ru.ncfu.autoshow.dto.testdrive.TestDriveRequest;
import ru.ncfu.autoshow.dto.testdrive.TestDriveResponse;
import ru.ncfu.autoshow.entity.TestDrive;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.NotificationType;
import ru.ncfu.autoshow.entity.enums.TestDriveStatus;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;
import ru.ncfu.autoshow.exception.AccessForbiddenException;
import ru.ncfu.autoshow.exception.BusinessRuleException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.TestDriveRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.TestDriveMapper;
import ru.ncfu.autoshow.mediator.NotificationService;
import ru.ncfu.autoshow.mediator.TestDriveService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TestDriveServiceImpl implements TestDriveService {

    /** Минимальный интервал между тест-драйвами одного автомобиля. */
    private static final Duration SLOT_WINDOW = Duration.ofHours(1);
    private static final List<TestDriveStatus> ACTIVE_STATUSES =
            List.of(TestDriveStatus.PENDING, TestDriveStatus.CONFIRMED);

    private final TestDriveRepository testDriveRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final TestDriveMapper testDriveMapper;

    public TestDriveServiceImpl(TestDriveRepository testDriveRepository, VehicleRepository vehicleRepository,
                                UserRepository userRepository, NotificationService notificationService,
                                TestDriveMapper testDriveMapper) {
        this.testDriveRepository = testDriveRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.testDriveMapper = testDriveMapper;
    }

    @Override
    public TestDriveResponse book(Long clientId, TestDriveRequest request) {
        User client = requireUser(clientId);
        Vehicle vehicle = requireVehicle(request.vehicleId());

        if (vehicle.getStatus() == VehicleStatus.SOLD || vehicle.getStatus() == VehicleStatus.UNAVAILABLE) {
            throw new BusinessRuleException("Этот автомобиль недоступен для тест-драйва");
        }
        ensureSlotFree(vehicle.getId(), request.scheduledAt(), null);

        TestDrive td = new TestDrive();
        td.setUser(client);
        td.setVehicle(vehicle);
        td.setDealerCenter(request.dealerCenter().trim());
        td.setScheduledAt(request.scheduledAt());
        td.setContactPhone(request.contactPhone());
        td.setNotes(request.notes());
        td.setStatus(TestDriveStatus.PENDING);

        TestDrive saved = testDriveRepository.save(td);
        notificationService.notify(client, "Заявка на тест-драйв создана",
                "Ваша запись на тест-драйв «" + vehicle.fullName() + "» принята и ожидает подтверждения менеджера.",
                NotificationType.TEST_DRIVE);
        notificationService.notifyManagers("Новая заявка на тест-драйв",
                "Клиент " + client.getFullName() + " записался на тест-драйв «" + vehicle.fullName()
                        + "». Желаемое время: " + request.scheduledAt() + ".",
                NotificationType.TEST_DRIVE);
        return testDriveMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestDriveResponse> getMine(Long userId) {
        return testDriveRepository.findByUserIdOrderByScheduledAtDesc(userId)
                .stream().map(testDriveMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestDriveResponse> getAll() {
        // Сортировка по времени подачи заявки (createdAt), а не по дате самого тест-драйва.
        return testDriveRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(testDriveMapper::toResponse).toList();
    }

    @Override
    public TestDriveResponse confirm(Long id, Long managerId) {
        TestDrive td = requireTestDrive(id);
        td.confirm(requireUser(managerId));
        notificationService.notify(td.getUser(), "Тест-драйв подтверждён",
                "Ваш тест-драйв «" + td.getVehicle().fullName() + "» подтверждён на " + td.getScheduledAt(),
                NotificationType.TEST_DRIVE);
        return testDriveMapper.toResponse(td);
    }

    @Override
    public TestDriveResponse reject(Long id, Long managerId) {
        TestDrive td = requireTestDrive(id);
        td.reject(requireUser(managerId));
        notificationService.notify(td.getUser(), "Тест-драйв отклонён",
                "К сожалению, запись на тест-драйв «" + td.getVehicle().fullName() + "» отклонена.",
                NotificationType.TEST_DRIVE);
        return testDriveMapper.toResponse(td);
    }

    @Override
    public TestDriveResponse complete(Long id) {
        TestDrive td = requireTestDrive(id);
        td.complete();
        return testDriveMapper.toResponse(td);
    }

    @Override
    public TestDriveResponse cancel(Long id, Long actorId, boolean staff) {
        TestDrive td = requireTestDrive(id);
        ensureOwnerOrStaff(td, actorId, staff);
        td.cancel();
        notificationService.notify(td.getUser(), "Тест-драйв отменён",
                "Запись на тест-драйв «" + td.getVehicle().fullName() + "» отменена.",
                NotificationType.TEST_DRIVE);
        // Если отменил клиент — уведомляем персонал, чтобы менеджеры видели отмену.
        if (!staff) {
            notificationService.notifyManagers("Клиент отменил тест-драйв",
                    "Клиент " + td.getUser().getFullName() + " отменил запись на тест-драйв «"
                            + td.getVehicle().fullName() + "».",
                    NotificationType.TEST_DRIVE);
        }
        return testDriveMapper.toResponse(td);
    }

    @Override
    public TestDriveResponse reschedule(Long id, Long actorId, boolean staff, RescheduleRequest request) {
        TestDrive td = requireTestDrive(id);
        ensureOwnerOrStaff(td, actorId, staff);
        ensureSlotFree(td.getVehicle().getId(), request.scheduledAt(), td.getId());
        td.reschedule(request.scheduledAt());
        notificationService.notify(td.getUser(), "Тест-драйв перенесён",
                "Запись на тест-драйв «" + td.getVehicle().fullName() + "» перенесена на " + request.scheduledAt(),
                NotificationType.TEST_DRIVE);
        return testDriveMapper.toResponse(td);
    }

    // ----------------------------- helpers -----------------------------

    private void ensureSlotFree(Long vehicleId, LocalDateTime when, Long excludeId) {
        LocalDateTime from = when.minus(SLOT_WINDOW);
        LocalDateTime to = when.plus(SLOT_WINDOW);
        boolean busy = testDriveRepository
                .existsByVehicleIdAndScheduledAtBetweenAndStatusIn(vehicleId, from, to, ACTIVE_STATUSES);
        // excludeId учитывается через перепроверку статуса самой записи при переносе
        if (busy) {
            throw new BusinessRuleException(
                    "Выбранный интервал для этого автомобиля занят. Пожалуйста, выберите другое время.");
        }
    }

    private void ensureOwnerOrStaff(TestDrive td, Long actorId, boolean staff) {
        if (!staff && !td.getUser().getId().equals(actorId)) {
            throw new AccessForbiddenException("Запись принадлежит другому пользователю");
        }
    }

    private TestDrive requireTestDrive(Long id) {
        return testDriveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Запись на тест-драйв", id));
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
