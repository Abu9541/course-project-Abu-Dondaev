package ru.ncfu.autoshow.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.enums.Role;

import java.util.List;
import java.util.Optional;

/**
 * Foundation: репозиторий пользователей.
 * Реализация интерфейса доступа к данным (Spring Data JPA = Data Mapper).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByActiveTrue();
}
