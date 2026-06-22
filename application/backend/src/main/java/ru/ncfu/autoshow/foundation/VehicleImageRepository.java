package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.VehicleImage;

import java.util.List;

/** Foundation: репозиторий изображений автомобилей. */
@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {

    List<VehicleImage> findByVehicleIdOrderBySortOrderAsc(Long vehicleId);
}
