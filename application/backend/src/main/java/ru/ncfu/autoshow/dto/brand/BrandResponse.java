package ru.ncfu.autoshow.dto.brand;

/** Представление марки автомобиля. */
public record BrandResponse(
        Long id,
        String name,
        String country,
        String logoUrl
) {
}
