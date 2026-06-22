package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.user.UpdateProfileRequest;
import ru.ncfu.autoshow.dto.user.UserResponse;
import ru.ncfu.autoshow.entity.enums.Role;

import java.util.List;

/** Mediator: управление пользователями и профилем. */
public interface UserService {

    UserResponse getById(Long id);

    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    List<UserResponse> getAll();

    UserResponse updateRole(Long userId, Role role);

    UserResponse setActive(Long userId, boolean active);
}
