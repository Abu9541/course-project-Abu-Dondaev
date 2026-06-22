package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.brand.BrandRequest;
import ru.ncfu.autoshow.dto.brand.BrandResponse;
import ru.ncfu.autoshow.dto.common.MessageResponse;
import ru.ncfu.autoshow.mediator.BrandService;

import java.util.List;

/** Control: марки автомобилей (просмотр — публично, изменение — для персонала). */
@Tag(name = "Марки", description = "Справочник марок автомобилей")
@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @Operation(summary = "Список всех марок")
    @GetMapping
    public List<BrandResponse> getAll() {
        return brandService.getAll();
    }

    @Operation(summary = "Марка по идентификатору")
    @GetMapping("/{id}")
    public BrandResponse getById(@PathVariable Long id) {
        return brandService.getById(id);
    }

    @Operation(summary = "Создание марки (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.create(request));
    }

    @Operation(summary = "Обновление марки (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PutMapping("/{id}")
    public BrandResponse update(@PathVariable Long id, @Valid @RequestBody BrandRequest request) {
        return brandService.update(id, request);
    }

    @Operation(summary = "Удаление марки (админ)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse delete(@PathVariable Long id) {
        brandService.delete(id);
        return new MessageResponse("Марка удалена");
    }
}
