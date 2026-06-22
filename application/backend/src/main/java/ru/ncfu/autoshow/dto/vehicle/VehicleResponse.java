package ru.ncfu.autoshow.dto.vehicle;

import ru.ncfu.autoshow.dto.brand.BrandResponse;
import ru.ncfu.autoshow.entity.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Полное представление автомобиля для карточки с подробной информацией. */
public record VehicleResponse(
        Long id,
        BrandResponse brand,
        String model,
        Integer year,
        String vin,
        BigDecimal price,
        BodyType bodyType,
        EngineType engineType,
        Transmission transmission,
        DriveType driveType,
        String color,
        Integer mileage,
        Integer powerHp,
        BigDecimal engineVolume,
        BigDecimal fuelConsumption,
        String equipmentLevel,
        String description,
        String imageUrl,
        VehicleStatus status,
        List<String> images,
        Double averageRating,
        long reviewCount,
        LocalDateTime createdAt
) {
}
