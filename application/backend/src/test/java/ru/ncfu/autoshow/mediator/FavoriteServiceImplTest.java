package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.autoshow.dto.vehicle.VehicleSummaryResponse;
import ru.ncfu.autoshow.entity.Favorite;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;
import ru.ncfu.autoshow.foundation.FavoriteRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.VehicleMapper;
import ru.ncfu.autoshow.mediator.impl.FavoriteServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис избранного")
class FavoriteServiceImplTest {

    @Mock FavoriteRepository favoriteRepository;
    @Mock UserRepository userRepository;
    @Mock VehicleRepository vehicleRepository;
    @Mock VehicleMapper vehicleMapper;

    FavoriteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FavoriteServiceImpl(favoriteRepository, userRepository, vehicleRepository, vehicleMapper);
    }

    private Vehicle vehicle(VehicleStatus status) {
        Vehicle v = new Vehicle();
        v.setStatus(status);
        return v;
    }

    @Test
    @DisplayName("getMine исключает проданные автомобили")
    void getMineExcludesSold() {
        User user = new User();
        Favorite available = new Favorite(user, vehicle(VehicleStatus.IN_STOCK));
        Favorite sold = new Favorite(user, vehicle(VehicleStatus.SOLD));
        when(favoriteRepository.findByUserIdOrderByCreatedAtDesc(4L))
                .thenReturn(List.of(available, sold));

        List<VehicleSummaryResponse> result = service.getMine(4L);

        assertEquals(1, result.size()); // проданный отфильтрован
    }

    @Test
    @DisplayName("Повторное добавление в избранное идемпотентно (без дубля)")
    void addIsIdempotent() {
        when(favoriteRepository.existsByUserIdAndVehicleId(4L, 1L)).thenReturn(true);

        service.add(4L, 1L);

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Добавление нового автомобиля сохраняет запись")
    void addNewSaves() {
        when(favoriteRepository.existsByUserIdAndVehicleId(4L, 1L)).thenReturn(false);
        when(userRepository.findById(4L)).thenReturn(Optional.of(new User()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(VehicleStatus.IN_STOCK)));

        service.add(4L, 1L);

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("Удаление делегируется репозиторию")
    void removeDelegates() {
        service.remove(4L, 1L);
        verify(favoriteRepository).deleteByUserIdAndVehicleId(4L, 1L);
    }
}
