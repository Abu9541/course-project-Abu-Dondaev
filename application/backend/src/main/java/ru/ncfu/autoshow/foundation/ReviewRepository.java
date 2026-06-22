package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.Review;

import java.util.List;

/** Foundation: репозиторий отзывов. */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);

    boolean existsByUserIdAndVehicleId(Long userId, Long vehicleId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vehicle.id = :vehicleId")
    Double averageRating(@Param("vehicleId") Long vehicleId);

    long countByVehicleId(Long vehicleId);
}
