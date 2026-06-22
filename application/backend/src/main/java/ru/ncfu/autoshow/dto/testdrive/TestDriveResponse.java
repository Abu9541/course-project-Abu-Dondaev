package ru.ncfu.autoshow.dto.testdrive;

import ru.ncfu.autoshow.entity.enums.TestDriveStatus;

import java.time.LocalDateTime;

/** Представление записи на тест-драйв. */
public record TestDriveResponse(
        Long id,
        Long userId,
        String userName,
        Long vehicleId,
        String vehicleName,
        String vehicleImageUrl,
        Long managerId,
        String managerName,
        String dealerCenter,
        LocalDateTime scheduledAt,
        TestDriveStatus status,
        String contactPhone,
        String notes,
        LocalDateTime createdAt
) {
}
