package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.autoshow.dto.user.UpdateProfileRequest;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.enums.Role;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.mapper.UserMapper;
import ru.ncfu.autoshow.mediator.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис пользователей")
class UserServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;

    UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(userRepository, userMapper);
    }

    private User user(long id, Role role) {
        User u = new User();
        u.setId(id);
        u.setRole(role);
        return u;
    }

    @Test
    @DisplayName("Запрос несуществующего пользователя приводит к 404")
    void getByIdNotFoundThrows() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    @DisplayName("Смена роли применяется к пользователю")
    void updateRoleChangesRole() {
        User u = user(4L, Role.CLIENT);
        when(userRepository.findById(4L)).thenReturn(Optional.of(u));

        service.updateRole(4L, Role.MANAGER);

        assertEquals(Role.MANAGER, u.getRole());
    }

    @Test
    @DisplayName("Блокировка пользователя снимает признак активности")
    void setActiveDeactivates() {
        User u = user(4L, Role.CLIENT);
        when(userRepository.findById(4L)).thenReturn(Optional.of(u));

        service.setActive(4L, false);

        assertFalse(u.isActive());
    }

    @Test
    @DisplayName("Обновление профиля меняет ФИО и телефон")
    void updateProfileUpdatesFields() {
        User u = user(4L, Role.CLIENT);
        when(userRepository.findById(4L)).thenReturn(Optional.of(u));

        service.updateProfile(4L, new UpdateProfileRequest("Новое Имя", "+79991234567"));

        assertEquals("Новое Имя", u.getFullName());
        assertEquals("+79991234567", u.getPhone());
    }

    @Test
    @DisplayName("getAll отображает всех пользователей")
    void getAllMapsAll() {
        when(userRepository.findAll()).thenReturn(List.of(user(1L, Role.ADMIN), user(2L, Role.CLIENT)));
        assertEquals(2, service.getAll().size());
    }
}
