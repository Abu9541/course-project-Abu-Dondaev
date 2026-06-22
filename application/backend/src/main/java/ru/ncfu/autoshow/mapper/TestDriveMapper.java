package ru.ncfu.autoshow.mapper;

import org.springframework.stereotype.Component;
import ru.ncfu.autoshow.dto.testdrive.TestDriveResponse;
import ru.ncfu.autoshow.entity.TestDrive;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;

/** Data Mapper для записей на тест-драйв. */
@Component
public class TestDriveMapper {

    public TestDriveResponse toResponse(TestDrive t) {
        User user = t.getUser();
        Vehicle vehicle = t.getVehicle();
        User manager = t.getManager();
        return new TestDriveResponse(
                t.getId(),
                user != null ? user.getId() : null,
                user != null ? user.getFullName() : null,
                vehicle != null ? vehicle.getId() : null,
                vehicle != null ? vehicle.fullName() : null,
                vehicle != null ? vehicle.getImageUrl() : null,
                manager != null ? manager.getId() : null,
                manager != null ? manager.getFullName() : null,
                t.getDealerCenter(),
                t.getScheduledAt(),
                t.getStatus(),
                t.getContactPhone(),
                t.getNotes(),
                t.getCreatedAt()
        );
    }
}
