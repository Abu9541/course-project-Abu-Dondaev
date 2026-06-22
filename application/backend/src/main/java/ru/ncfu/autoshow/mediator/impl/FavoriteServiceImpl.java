package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.vehicle.VehicleSummaryResponse;
import ru.ncfu.autoshow.entity.Favorite;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.FavoriteRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.VehicleMapper;
import ru.ncfu.autoshow.mediator.FavoriteService;

import java.util.List;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository,
                               VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleSummaryResponse> getMine(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(Favorite::getVehicle)
                .filter(v -> v.getStatus() != VehicleStatus.SOLD) // проданные авто не показываем в избранном
                .map(vehicleMapper::toSummary)
                .toList();
    }

    @Override
    public void add(Long userId, Long vehicleId) {
        if (favoriteRepository.existsByUserIdAndVehicleId(userId, vehicleId)) {
            return; // идемпотентно: уже в избранном
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", userId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Автомобиль", vehicleId));
        favoriteRepository.save(new Favorite(user, vehicle));
    }

    @Override
    public void remove(Long userId, Long vehicleId) {
        favoriteRepository.deleteByUserIdAndVehicleId(userId, vehicleId);
    }
}
