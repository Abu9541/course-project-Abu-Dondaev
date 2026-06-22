package ru.ncfu.autoshow.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.enums.Role;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Генерация и валидация JWT-токенов")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-secret-key-for-jwt-tokens-must-be-at-least-32-bytes-long", 3_600_000L);
    }

    private User user() {
        User u = new User();
        u.setId(42L);
        u.setEmail("client1@example.com");
        u.setRole(Role.CLIENT);
        return u;
    }

    @Test
    @DisplayName("Токен валиден, субъект соответствует email")
    void generateAndValidate() {
        String token = jwtService.generateToken(user());
        assertNotNull(token);
        assertTrue(jwtService.isValid(token));
        assertEquals("client1@example.com", jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("Некорректный токен не проходит валидацию")
    void invalidToken() {
        assertFalse(jwtService.isValid("not-a-real-token"));
    }
}
