package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.vehicle.VehicleSummaryResponse;

import java.util.List;

/** Mediator: избранные автомобили пользователя. */
public interface FavoriteService {

    List<VehicleSummaryResponse> getMine(Long userId);

    void add(Long userId, Long vehicleId);

    void remove(Long userId, Long vehicleId);
}
