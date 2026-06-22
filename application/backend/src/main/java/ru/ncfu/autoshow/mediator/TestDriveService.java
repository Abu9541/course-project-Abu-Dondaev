package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.testdrive.RescheduleRequest;
import ru.ncfu.autoshow.dto.testdrive.TestDriveRequest;
import ru.ncfu.autoshow.dto.testdrive.TestDriveResponse;

import java.util.List;

/** Mediator: запись на тест-драйв и управление её жизненным циклом. */
public interface TestDriveService {

    TestDriveResponse book(Long clientId, TestDriveRequest request);

    List<TestDriveResponse> getMine(Long userId);

    List<TestDriveResponse> getAll();

    TestDriveResponse confirm(Long id, Long managerId);

    TestDriveResponse reject(Long id, Long managerId);

    TestDriveResponse complete(Long id);

    TestDriveResponse cancel(Long id, Long actorId, boolean staff);

    TestDriveResponse reschedule(Long id, Long actorId, boolean staff, RescheduleRequest request);
}
