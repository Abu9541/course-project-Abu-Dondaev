package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.auth.AuthResponse;
import ru.ncfu.autoshow.dto.auth.LoginRequest;
import ru.ncfu.autoshow.dto.auth.RegisterRequest;

/** Mediator: аутентификация и регистрация (контракт Control → Mediator). */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
