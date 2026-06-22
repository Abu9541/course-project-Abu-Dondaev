package ru.ncfu.autoshow.dto.vehicle;

import ru.ncfu.autoshow.entity.enums.*;

import java.math.BigDecimal;

/** Краткое представление автомобиля для списка/каталога. */
public record VehicleSummaryResponse(
        Long id,
        String brandName,
        String model,
        Integer year,
        BigDecimal price,
        BodyType bodyType,
        EngineType engineType,
        Transmission transmission,
        DriveType driveType,
        Integer powerHp,
        String color,
        Integer mileage,
        String equipmentLevel,
        String imageUrl,
        VehicleStatus status
) {
}
