package ru.ncfu.autoshow.mediator.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.common.PageResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleRequest;
import ru.ncfu.autoshow.dto.vehicle.VehicleResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleSummaryResponse;
import ru.ncfu.autoshow.entity.Brand;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.VehicleImage;
import ru.ncfu.autoshow.entity.enums.BodyType;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;
import ru.ncfu.autoshow.exception.DuplicateResourceException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.BrandRepository;
import ru.ncfu.autoshow.foundation.ReviewRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.VehicleMapper;
import ru.ncfu.autoshow.mediator.VehicleService;

import java.math.BigDecimal;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final BrandRepository brandRepository;
    private final ReviewRepository reviewRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, BrandRepository brandRepository,
                              ReviewRepository reviewRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.brandRepository = brandRepository;
        this.reviewRepository = reviewRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VehicleSummaryResponse> search(String q, Long brandId, BodyType bodyType,
                                                       VehicleStatus status, BigDecimal minPrice,
                                                       BigDecimal maxPrice, Pageable pageable) {
        // Пустая строка (а не null) — чтобы PostgreSQL корректно вывел тип параметра в LOWER/LIKE
        String query = (q == null || q.isBlank()) ? "" : q.trim();
        // includeSold = false: проданные авто никогда не показываются в обычном каталоге.
        Page<VehicleSummaryResponse> page = vehicleRepository
                .search(query, brandId, bodyType, status, false, minPrice, maxPrice, pageable)
                .map(vehicleMapper::toSummary);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VehicleSummaryResponse> soldVehicles(Pageable pageable) {
        // Отдельный список проданных авто (для админа/персонала).
        return PageResponse.of(
                vehicleRepository.findByStatus(VehicleStatus.SOLD, pageable).map(vehicleMapper::toSummary));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getById(Long id) {
        Vehicle vehicle = requireVehicle(id);
        Double avg = reviewRepository.averageRating(id);
        long count = reviewRepository.countByVehicleId(id);
        return vehicleMapper.toResponse(vehicle, avg, count);
    }

    @Override
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByVin(request.vin())) {
            throw new DuplicateResourceException("Автомобиль с VIN " + request.vin() + " уже существует");
        }
        Vehicle vehicle = new Vehicle();
        applyRequest(vehicle, request);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(saved, null, 0);
    }

    @Override
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = requireVehicle(id);
        if (!vehicle.getVin().equalsIgnoreCase(request.vin()) && vehicleRepository.existsByVin(request.vin())) {
            throw new DuplicateResourceException("Автомобиль с VIN " + request.vin() + " уже существует");
        }
        applyRequest(vehicle, request);
        Double avg = reviewRepository.averageRating(id);
        long count = reviewRepository.countByVehicleId(id);
        return vehicleMapper.toResponse(vehicle, avg, count);
    }

    @Override
    public void delete(Long id) {
        Vehicle vehicle = requireVehicle(id);
        vehicleRepository.delete(vehicle);
    }

    // ----------------------------- helpers -----------------------------

    private void applyRequest(Vehicle vehicle, VehicleRequest r) {
        Brand brand = brandRepository.findById(r.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Марка", r.brandId()));
        vehicle.setBrand(brand);
        vehicle.setModel(r.model().trim());
        vehicle.setYear(r.year());
        vehicle.setVin(r.vin().toUpperCase());
        vehicle.setPrice(r.price());
        vehicle.setBodyType(r.bodyType());
        vehicle.setEngineType(r.engineType());
        vehicle.setTransmission(r.transmission());
        vehicle.setDriveType(r.driveType());
        vehicle.setColor(r.color().trim());
        vehicle.setMileage(r.mileage());
        vehicle.setPowerHp(r.powerHp());
        vehicle.setEngineVolume(r.engineVolume());
        vehicle.setFuelConsumption(r.fuelConsumption());
        vehicle.setEquipmentLevel(r.equipmentLevel());
        vehicle.setDescription(r.description());
        vehicle.setImageUrl(r.imageUrl());
        if (r.status() != null) {
            vehicle.setStatus(r.status());
        }

        // Перестроение фотогалереи
        if (r.images() != null) {
            vehicle.getImages().clear();
            int order = 0;
            for (String url : r.images()) {
                if (url != null && !url.isBlank()) {
                    vehicle.addImage(new VehicleImage(url.trim(), order++));
                }
            }
        }
    }

    private Vehicle requireVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автомобиль", id));
    }
}
