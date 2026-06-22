package ru.ncfu.autoshow.dto.vehicle;

import jakarta.validation.constraints.*;
import ru.ncfu.autoshow.entity.enums.*;

import java.math.BigDecimal;
import java.util.List;

/** Запрос создания/обновления автомобиля (доступно менеджеру/админу). */
public record VehicleRequest(
        @NotNull(message = "Марка обязательна")
        Long brandId,

        @NotBlank(message = "Модель обязательна")
        @Size(max = 120)
        String model,

        @NotNull(message = "Год обязателен")
        @Min(value = 1950, message = "Год не может быть раньше 1950")
        @Max(value = 2100, message = "Некорректный год")
        Integer year,

        @NotBlank(message = "VIN обязателен")
        @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN должен состоять из 17 символов (без I, O, Q)")
        String vin,

        @NotNull(message = "Цена обязательна")
        @Positive(message = "Цена должна быть положительной")
        BigDecimal price,

        @NotNull(message = "Тип кузова обязателен")
        BodyType bodyType,

        @NotNull(message = "Тип двигателя обязателен")
        EngineType engineType,

        @NotNull(message = "Тип КПП обязателен")
        Transmission transmission,

        @NotNull(message = "Тип привода обязателен")
        DriveType driveType,

        @NotBlank(message = "Цвет обязателен")
        @Size(max = 40)
        String color,

        @NotNull
        @PositiveOrZero(message = "Пробег не может быть отрицательным")
        Integer mileage,

        @NotNull(message = "Мощность обязательна")
        @Positive(message = "Мощность должна быть положительной")
        Integer powerHp,

        @PositiveOrZero
        BigDecimal engineVolume,

        @PositiveOrZero
        BigDecimal fuelConsumption,

        @Size(max = 60)
        String equipmentLevel,

        String description,

        @Size(max = 512)
        String imageUrl,

        VehicleStatus status,

        List<@Size(max = 512) String> images
) {
}
