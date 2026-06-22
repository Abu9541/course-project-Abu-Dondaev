package ru.ncfu.autoshow.mapper;

import org.springframework.stereotype.Component;
import ru.ncfu.autoshow.dto.brand.BrandRequest;
import ru.ncfu.autoshow.dto.brand.BrandResponse;
import ru.ncfu.autoshow.entity.Brand;

/** Data Mapper для марок автомобилей. */
@Component
public class BrandMapper {

    public BrandResponse toResponse(Brand brand) {
        return new BrandResponse(brand.getId(), brand.getName(), brand.getCountry(), brand.getLogoUrl());
    }

    /** Применяет данные запроса к сущности (создание/обновление). */
    public void apply(Brand brand, BrandRequest request) {
        brand.setName(request.name());
        brand.setCountry(request.country());
        brand.setLogoUrl(request.logoUrl());
    }
}
