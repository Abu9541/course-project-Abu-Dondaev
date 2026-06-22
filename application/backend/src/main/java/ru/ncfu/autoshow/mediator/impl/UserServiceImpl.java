package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.user.UpdateProfileRequest;
import ru.ncfu.autoshow.dto.user.UserResponse;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.enums.Role;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.mapper.UserMapper;
import ru.ncfu.autoshow.mediator.UserService;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toResponse(requireUser(id));
    }

    @Override
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = requireUser(userId);
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    @Override
    public UserResponse updateRole(Long userId, Role role) {
        User user = requireUser(userId);
        user.setRole(role);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse setActive(Long userId, boolean active) {
        User user = requireUser(userId);
        if (active) {
            user.activate();
        } else {
            user.deactivate();
        }
        return userMapper.toResponse(user);
    }

    private User requireUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", id));
    }
}
