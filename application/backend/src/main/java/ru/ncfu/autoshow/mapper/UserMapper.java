package ru.ncfu.autoshow.mapper;

import org.springframework.stereotype.Component;
import ru.ncfu.autoshow.dto.user.UserResponse;
import ru.ncfu.autoshow.entity.User;

/**
 * Data Mapper: преобразование сущности {@link User} в DTO.
 * Отделяет доменную модель от представления, передаваемого клиенту.
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getLoyaltyLevel(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
