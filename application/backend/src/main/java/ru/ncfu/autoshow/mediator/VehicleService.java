package ru.ncfu.autoshow.mediator;

import org.springframework.data.domain.Pageable;
import ru.ncfu.autoshow.dto.common.PageResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleRequest;
import ru.ncfu.autoshow.dto.vehicle.VehicleResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleSummaryResponse;
import ru.ncfu.autoshow.entity.enums.BodyType;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;

import java.math.BigDecimal;

/** Mediator: каталог автомобилей (поиск, фильтрация, CRUD). */
public interface VehicleService {

    PageResponse<VehicleSummaryResponse> search(String q, Long brandId, BodyType bodyType,
                                                VehicleStatus status, BigDecimal minPrice,
                                                BigDecimal maxPrice, Pageable pageable);

    /** Список проданных автомобилей (отдельная подстраница для персонала/админа). */
    PageResponse<VehicleSummaryResponse> soldVehicles(Pageable pageable);

    VehicleResponse getById(Long id);

    VehicleResponse create(VehicleRequest request);

    VehicleResponse update(Long id, VehicleRequest request);

    void delete(Long id);
}
