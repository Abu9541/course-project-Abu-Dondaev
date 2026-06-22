package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.autoshow.dto.testdrive.TestDriveRequest;
import ru.ncfu.autoshow.dto.testdrive.TestDriveResponse;
import ru.ncfu.autoshow.entity.Brand;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.Role;
import ru.ncfu.autoshow.entity.enums.TestDriveStatus;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;
import ru.ncfu.autoshow.exception.BusinessRuleException;
import ru.ncfu.autoshow.foundation.TestDriveRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.TestDriveMapper;
import ru.ncfu.autoshow.mediator.impl.TestDriveServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис тест-драйвов")
class TestDriveServiceImplTest {

    @Mock TestDriveRepository testDriveRepository;
    @Mock VehicleRepository vehicleRepository;
    @Mock UserRepository userRepository;
    @Mock NotificationService notificationService;

    TestDriveServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TestDriveServiceImpl(testDriveRepository, vehicleRepository, userRepository,
                notificationService, new TestDriveMapper());
    }

    private User client() {
        User u = new User();
        u.setId(4L);
        u.setRole(Role.CLIENT);
        return u;
    }

    private Vehicle vehicle(VehicleStatus status) {
        Vehicle v = new Vehicle();
        v.setId(1L);
        v.setBrand(new Brand("BMW", "Германия"));
        v.setModel("X5");
        v.setYear(2024);
        v.setStatus(status);
        return v;
    }

    private TestDriveRequest request() {
        return new TestDriveRequest(1L, "Автосалон на Ленина, 100",
                LocalDateTime.now().plusDays(2), "+79280000004", null);
    }

    @Test
    @DisplayName("Успешная запись создаётся в статусе PENDING")
    void bookSuccess() {
        when(userRepository.findById(4L)).thenReturn(Optional.of(client()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(VehicleStatus.IN_STOCK)));
        when(testDriveRepository.existsByVehicleIdAndScheduledAtBetweenAndStatusIn(anyLong(), any(), any(), anyList()))
                .thenReturn(false);
        when(testDriveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TestDriveResponse resp = service.book(4L, request());

        assertEquals(TestDriveStatus.PENDING, resp.status());
        verify(notificationService).notify(any(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Занятый слот приводит к нарушению бизнес-правила")
    void bookSlotBusyThrows() {
        when(userRepository.findById(4L)).thenReturn(Optional.of(client()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(VehicleStatus.IN_STOCK)));
        when(testDriveRepository.existsByVehicleIdAndScheduledAtBetweenAndStatusIn(anyLong(), any(), any(), anyList()))
                .thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> service.book(4L, request()));
    }

    @Test
    @DisplayName("Нельзя записаться на тест-драйв проданного автомобиля")
    void bookSoldVehicleThrows() {
        when(userRepository.findById(4L)).thenReturn(Optional.of(client()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(VehicleStatus.SOLD)));

        assertThrows(BusinessRuleException.class, () -> service.book(4L, request()));
    }
}
