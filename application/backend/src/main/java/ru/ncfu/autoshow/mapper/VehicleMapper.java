package ru.ncfu.autoshow.mapper;

import org.springframework.stereotype.Component;
import ru.ncfu.autoshow.dto.brand.BrandResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleSummaryResponse;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.VehicleImage;

import java.util.Comparator;
import java.util.List;

/** Data Mapper для автомобилей (краткое и полное представление). */
@Component
public class VehicleMapper {

    private final BrandMapper brandMapper;

    public VehicleMapper(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    public VehicleSummaryResponse toSummary(Vehicle v) {
        return new VehicleSummaryResponse(
                v.getId(),
                v.getBrand() != null ? v.getBrand().getName() : null,
                v.getModel(),
                v.getYear(),
                v.getPrice(),
                v.getBodyType(),
                v.getEngineType(),
                v.getTransmission(),
                v.getDriveType(),
                v.getPowerHp(),
                v.getColor(),
                v.getMileage(),
                v.getEquipmentLevel(),
                v.getImageUrl(),
                v.getStatus()
        );
    }

    public VehicleResponse toResponse(Vehicle v, Double averageRating, long reviewCount) {
        List<String> imageUrls = v.getImages().stream()
                .sorted(Comparator.comparing(VehicleImage::getSortOrder))
                .map(VehicleImage::getUrl)
                .toList();

        BrandResponse brand = v.getBrand() != null ? brandMapper.toResponse(v.getBrand()) : null;

        return new VehicleResponse(
                v.getId(),
                brand,
                v.getModel(),
                v.getYear(),
                v.getVin(),
                v.getPrice(),
                v.getBodyType(),
                v.getEngineType(),
                v.getTransmission(),
                v.getDriveType(),
                v.getColor(),
                v.getMileage(),
                v.getPowerHp(),
                v.getEngineVolume(),
                v.getFuelConsumption(),
                v.getEquipmentLevel(),
                v.getDescription(),
                v.getImageUrl(),
                v.getStatus(),
                imageUrls,
                averageRating != null ? Math.round(averageRating * 10) / 10.0 : null,
                reviewCount,
                v.getCreatedAt()
        );
    }
}
