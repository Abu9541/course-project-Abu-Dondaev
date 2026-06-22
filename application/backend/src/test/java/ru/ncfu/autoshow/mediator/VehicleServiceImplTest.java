package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.autoshow.dto.vehicle.VehicleRequest;
import ru.ncfu.autoshow.dto.vehicle.VehicleResponse;
import ru.ncfu.autoshow.entity.Brand;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.*;
import ru.ncfu.autoshow.exception.DuplicateResourceException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.BrandRepository;
import ru.ncfu.autoshow.foundation.ReviewRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.BrandMapper;
import ru.ncfu.autoshow.mapper.VehicleMapper;
import ru.ncfu.autoshow.mediator.impl.VehicleServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис каталога автомобилей")
class VehicleServiceImplTest {

    @Mock VehicleRepository vehicleRepository;
    @Mock BrandRepository brandRepository;
    @Mock ReviewRepository reviewRepository;

    VehicleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VehicleServiceImpl(vehicleRepository, brandRepository, reviewRepository,
                new VehicleMapper(new BrandMapper()));
    }

    private Vehicle vehicle() {
        Vehicle v = new Vehicle();
        v.setId(1L);
        v.setBrand(new Brand("Toyota", "Япония"));
        v.setModel("Camry");
        v.setYear(2024);
        v.setVin("JTNB11HK0P3100007");
        v.setPrice(new BigDecimal("3590000"));
        v.setBodyType(BodyType.SEDAN);
        v.setEngineType(EngineType.PETROL);
        v.setTransmission(Transmission.AUTOMATIC);
        v.setDriveType(DriveType.FWD);
        v.setColor("Белый");
        v.setPowerHp(200);
        v.setStatus(VehicleStatus.IN_STOCK);
        return v;
    }

    private VehicleRequest request(String vin) {
        return new VehicleRequest(1L, "Camry", 2024, vin, new BigDecimal("3590000"),
                BodyType.SEDAN, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.FWD,
                "Белый", 0, 200, new BigDecimal("2.5"), new BigDecimal("7.4"),
                "Prestige", "Описание", null, VehicleStatus.IN_STOCK, null);
    }

    @Test
    @DisplayName("Карточка автомобиля включает средний рейтинг и число отзывов")
    void getByIdIncludesRating() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle()));
        when(reviewRepository.averageRating(1L)).thenReturn(4.5);
        when(reviewRepository.countByVehicleId(1L)).thenReturn(2L);

        VehicleResponse r = service.getById(1L);

        assertEquals(4.5, r.averageRating());
        assertEquals(2, r.reviewCount());
        assertEquals("Camry", r.model());
        assertEquals("Toyota", r.brand().name());
    }

    @Test
    @DisplayName("Создание автомобиля с существующим VIN отклоняется")
    void createDuplicateVinThrows() {
        when(vehicleRepository.existsByVin("JTNB11HK0P3100007")).thenReturn(true);
        assertThrows(DuplicateResourceException.class,
                () -> service.create(request("JTNB11HK0P3100007")));
    }

    @Test
    @DisplayName("Запрос несуществующего автомобиля приводит к 404")
    void getByIdNotFoundThrows() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }
}
