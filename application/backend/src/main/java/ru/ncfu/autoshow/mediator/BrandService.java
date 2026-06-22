package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.brand.BrandRequest;
import ru.ncfu.autoshow.dto.brand.BrandResponse;

import java.util.List;

/** Mediator: управление марками автомобилей. */
public interface BrandService {

    List<BrandResponse> getAll();

    BrandResponse getById(Long id);

    BrandResponse create(BrandRequest request);

    BrandResponse update(Long id, BrandRequest request);

    void delete(Long id);
}
