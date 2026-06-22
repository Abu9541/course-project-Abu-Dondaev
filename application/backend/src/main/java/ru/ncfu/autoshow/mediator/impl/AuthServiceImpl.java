package ru.ncfu.autoshow.mediator.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.auth.AuthResponse;
import ru.ncfu.autoshow.dto.auth.LoginRequest;
import ru.ncfu.autoshow.dto.auth.RegisterRequest;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.enums.LoyaltyLevel;
import ru.ncfu.autoshow.entity.enums.Role;
import ru.ncfu.autoshow.exception.DuplicateResourceException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.mapper.UserMapper;
import ru.ncfu.autoshow.mediator.AuthService;
import ru.ncfu.autoshow.security.JwtService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtService jwtService, AuthenticationManager authenticationManager,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Пользователь с таким email уже зарегистрирован");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(Role.CLIENT);
        user.setLoyaltyLevel(LoyaltyLevel.STANDARD);
        user.setPdnConsent(request.pdnConsent());
        user.setActive(true);

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);
        return AuthResponse.of(token, jwtService.getExpirationMs(), userMapper.toResponse(saved));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        // Бросает AuthenticationException при неверных учётных данных (→ 401 в GlobalExceptionHandler)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", email));
        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, jwtService.getExpirationMs(), userMapper.toResponse(user));
    }
}
