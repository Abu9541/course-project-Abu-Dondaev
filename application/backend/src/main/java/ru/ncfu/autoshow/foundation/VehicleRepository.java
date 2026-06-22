package ru.ncfu.autoshow.foundation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.BodyType;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;

import java.math.BigDecimal;

/** Foundation: репозиторий автомобилей с динамической фильтрацией и поиском. */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Поиск и фильтрация каталога. Любой из параметров может быть {@code null}
     * (тогда соответствующее условие не применяется). Поддерживает пагинацию.
     */
    @Query("""
            SELECT v FROM Vehicle v
            WHERE (:brandId  IS NULL OR v.brand.id   = :brandId)
              AND (:bodyType IS NULL OR v.bodyType   = :bodyType)
              AND (:status   IS NULL OR v.status     = :status)
              AND (:includeSold = TRUE OR v.status <> ru.ncfu.autoshow.entity.enums.VehicleStatus.SOLD)
              AND (:minPrice IS NULL OR v.price     >= :minPrice)
              AND (:maxPrice IS NULL OR v.price     <= :maxPrice)
              AND (:q = ''
                   OR LOWER(v.model)      LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(v.brand.name) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<Vehicle> search(@Param("q") String q,
                         @Param("brandId") Long brandId,
                         @Param("bodyType") BodyType bodyType,
                         @Param("status") VehicleStatus status,
                         @Param("includeSold") boolean includeSold,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);

    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);

    long countByStatus(VehicleStatus status);

    boolean existsByVin(String vin);
}
