package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ncfu.autoshow.dto.auth.AuthResponse;
import ru.ncfu.autoshow.dto.auth.RegisterRequest;
import ru.ncfu.autoshow.entity.enums.Role;
import ru.ncfu.autoshow.exception.DuplicateResourceException;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.mapper.UserMapper;
import ru.ncfu.autoshow.mediator.impl.AuthServiceImpl;
import ru.ncfu.autoshow.security.JwtService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис аутентификации")
class AuthServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;

    AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(userRepository, passwordEncoder, jwtService,
                authenticationManager, new UserMapper());
    }

    @Test
    @DisplayName("Регистрация создаёт клиента и нормализует email")
    void registerSuccess() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123")).thenReturn("HASH");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token-123");
        when(jwtService.getExpirationMs()).thenReturn(86_400_000L);

        AuthResponse resp = service.register(
                new RegisterRequest("Новый Клиент", "New@example.com", "Secret123", "+79280000000", true));

        assertEquals("token-123", resp.token());
        assertEquals("Bearer", resp.tokenType());
        assertEquals("new@example.com", resp.user().email());
        assertEquals(Role.CLIENT, resp.user().role());
    }

    @Test
    @DisplayName("Повторная регистрация по существующему email отклоняется")
    void registerDuplicateThrows() {
        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.register(
                new RegisterRequest("Дубликат", "dup@example.com", "Secret123", null, true)));
    }
}
