package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.brand.BrandRequest;
import ru.ncfu.autoshow.dto.brand.BrandResponse;
import ru.ncfu.autoshow.entity.Brand;
import ru.ncfu.autoshow.exception.DuplicateResourceException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.BrandRepository;
import ru.ncfu.autoshow.mapper.BrandMapper;
import ru.ncfu.autoshow.mediator.BrandService;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAll() {
        return brandRepository.findAll().stream().map(brandMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getById(Long id) {
        return brandMapper.toResponse(requireBrand(id));
    }

    @Override
    public BrandResponse create(BrandRequest request) {
        if (brandRepository.existsByName(request.name().trim())) {
            throw new DuplicateResourceException("Марка с названием «" + request.name() + "» уже существует");
        }
        Brand brand = new Brand();
        brandMapper.apply(brand, request);
        brand.setName(request.name().trim());
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = requireBrand(id);
        String newName = request.name().trim();
        if (!brand.getName().equalsIgnoreCase(newName) && brandRepository.existsByName(newName)) {
            throw new DuplicateResourceException("Марка с названием «" + newName + "» уже существует");
        }
        brandMapper.apply(brand, request);
        brand.setName(newName);
        return brandMapper.toResponse(brand);
    }

    @Override
    public void delete(Long id) {
        Brand brand = requireBrand(id);
        brandRepository.delete(brand);
    }

    private Brand requireBrand(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Марка", id));
    }
}
