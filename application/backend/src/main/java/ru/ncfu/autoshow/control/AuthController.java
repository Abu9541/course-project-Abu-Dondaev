package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.auth.AuthResponse;
import ru.ncfu.autoshow.dto.auth.LoginRequest;
import ru.ncfu.autoshow.dto.auth.RegisterRequest;
import ru.ncfu.autoshow.mediator.AuthService;

/** Control: аутентификация и регистрация. */
@Tag(name = "Аутентификация", description = "Регистрация и вход в систему")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Регистрация нового клиента")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Аутентификация (вход) и получение JWT-токена")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
