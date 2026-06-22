package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.user.UpdateProfileRequest;
import ru.ncfu.autoshow.dto.user.UserResponse;
import ru.ncfu.autoshow.mediator.UserService;
import ru.ncfu.autoshow.security.CustomUserDetails;

/** Control: профиль текущего пользователя. */
@Tag(name = "Пользователи", description = "Профиль текущего пользователя")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Текущий пользователь (профиль)")
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal CustomUserDetails principal) {
        return userService.getById(principal.getId());
    }

    @Operation(summary = "Обновление собственного профиля")
    @PutMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal CustomUserDetails principal,
                                 @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(principal.getId(), request);
    }
}
