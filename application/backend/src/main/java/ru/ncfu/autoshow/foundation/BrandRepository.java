package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.Brand;

import java.util.Optional;

/** Foundation: репозиторий марок автомобилей. */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    boolean existsByName(String name);
}
