package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.common.MessageResponse;
import ru.ncfu.autoshow.dto.common.PageResponse;
import ru.ncfu.autoshow.dto.review.ReviewRequest;
import ru.ncfu.autoshow.dto.review.ReviewResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleRequest;
import ru.ncfu.autoshow.dto.vehicle.VehicleResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleSummaryResponse;
import ru.ncfu.autoshow.entity.enums.BodyType;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;
import ru.ncfu.autoshow.mediator.ReviewService;
import ru.ncfu.autoshow.mediator.VehicleService;
import ru.ncfu.autoshow.security.CustomUserDetails;

import java.math.BigDecimal;
import java.util.List;

/** Control: каталог автомобилей, карточки и отзывы. */
@Tag(name = "Автомобили", description = "Каталог, поиск, фильтрация, карточки и отзывы")
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final ReviewService reviewService;

    public VehicleController(VehicleService vehicleService, ReviewService reviewService) {
        this.vehicleService = vehicleService;
        this.reviewService = reviewService;
    }

    @Operation(summary = "Каталог с поиском, фильтрацией и пагинацией")
    @GetMapping
    public PageResponse<VehicleSummaryResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BodyType bodyType,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(dir, sortBy));
        return vehicleService.search(q, brandId, bodyType, status, minPrice, maxPrice, pageable);
    }

    @Operation(summary = "Список проданных автомобилей (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/sold")
    public PageResponse<VehicleSummaryResponse> sold(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "id"));
        return vehicleService.soldVehicles(pageable);
    }

    @Operation(summary = "Карточка автомобиля по идентификатору")
    @GetMapping("/{id}")
    public VehicleResponse getById(@PathVariable Long id) {
        return vehicleService.getById(id);
    }

    @Operation(summary = "Добавление автомобиля (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @Operation(summary = "Обновление автомобиля (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PutMapping("/{id}")
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return vehicleService.update(id, request);
    }

    @Operation(summary = "Удаление автомобиля (админ)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return new MessageResponse("Автомобиль удалён");
    }

    // ----------------------------- отзывы -----------------------------

    @Operation(summary = "Отзывы об автомобиле")
    @GetMapping("/{id}/reviews")
    public List<ReviewResponse> reviews(@PathVariable Long id) {
        return reviewService.getByVehicle(id);
    }

    @Operation(summary = "Добавить отзыв об автомобиле")
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponse> addReview(@PathVariable Long id,
                                                    @AuthenticationPrincipal CustomUserDetails principal,
                                                    @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.add(principal.getId(), id, request));
    }

    @Operation(summary = "Удалить отзыв (автор или персонал)")
    @DeleteMapping("/reviews/{reviewId}")
    public MessageResponse deleteReview(@PathVariable Long reviewId,
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        reviewService.delete(reviewId, principal.getId(), principal.getUser().isStaff());
        return new MessageResponse("Отзыв удалён");
    }
}
