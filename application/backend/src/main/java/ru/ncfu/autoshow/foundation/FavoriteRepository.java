package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.Favorite;

import java.util.List;
import java.util.Optional;

/** Foundation: репозиторий избранного. */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Favorite> findByUserIdAndVehicleId(Long userId, Long vehicleId);

    boolean existsByUserIdAndVehicleId(Long userId, Long vehicleId);

    void deleteByUserIdAndVehicleId(Long userId, Long vehicleId);

    /** Удалить автомобиль из избранного у всех пользователей (например, когда он продан). */
    void deleteByVehicleId(Long vehicleId);
}
