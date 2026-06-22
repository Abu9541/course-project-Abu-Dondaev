package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.common.MessageResponse;
import ru.ncfu.autoshow.dto.vehicle.VehicleSummaryResponse;
import ru.ncfu.autoshow.mediator.FavoriteService;
import ru.ncfu.autoshow.security.CustomUserDetails;

import java.util.List;

/** Control: избранные автомобили. */
@Tag(name = "Избранное", description = "Избранные автомобили пользователя")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "Мои избранные автомобили")
    @GetMapping
    public List<VehicleSummaryResponse> my(@AuthenticationPrincipal CustomUserDetails principal) {
        return favoriteService.getMine(principal.getId());
    }

    @Operation(summary = "Добавить в избранное")
    @PostMapping("/{vehicleId}")
    public MessageResponse add(@PathVariable Long vehicleId, @AuthenticationPrincipal CustomUserDetails principal) {
        favoriteService.add(principal.getId(), vehicleId);
        return new MessageResponse("Добавлено в избранное");
    }

    @Operation(summary = "Удалить из избранного")
    @DeleteMapping("/{vehicleId}")
    public MessageResponse remove(@PathVariable Long vehicleId, @AuthenticationPrincipal CustomUserDetails principal) {
        favoriteService.remove(principal.getId(), vehicleId);
        return new MessageResponse("Удалено из избранного");
    }
}
