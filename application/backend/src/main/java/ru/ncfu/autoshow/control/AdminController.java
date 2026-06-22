package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.user.UpdateRoleRequest;
import ru.ncfu.autoshow.dto.user.UserResponse;
import ru.ncfu.autoshow.mediator.UserService;

import java.util.List;

/**
 * Control: администрирование пользователей.
 * Весь путь {@code /api/admin/**} защищён ролью ADMIN в SecurityConfig.
 */
@Tag(name = "Администрирование", description = "Управление пользователями (только администратор)")
@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Список всех пользователей")
    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @Operation(summary = "Изменить роль пользователя")
    @PutMapping("/{id}/role")
    public UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return userService.updateRole(id, request.role());
    }

    @Operation(summary = "Активировать/заблокировать пользователя")
    @PutMapping("/{id}/status")
    public UserResponse setStatus(@PathVariable Long id, @RequestParam boolean active) {
        return userService.setActive(id, active);
    }
}
