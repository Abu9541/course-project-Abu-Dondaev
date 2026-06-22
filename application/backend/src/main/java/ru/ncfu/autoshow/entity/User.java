package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ncfu.autoshow.entity.enums.LoyaltyLevel;
import ru.ncfu.autoshow.entity.enums.Role;

/**
 * Пользователь системы. Содержит учётные данные, роль и бизнес-методы
 * проверки прав доступа (сущность не анемичная).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.CLIENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "loyalty_level", nullable = false, length = 20)
    private LoyaltyLevel loyaltyLevel = LoyaltyLevel.STANDARD;

    @Column(name = "pdn_consent", nullable = false)
    private boolean pdnConsent = false;

    @Column(nullable = false)
    private boolean active = true;

    // ----------------------------- бизнес-методы -----------------------------

    public boolean isAdmin()   { return role == Role.ADMIN; }
    public boolean isManager() { return role == Role.MANAGER; }
    public boolean isClient()  { return role == Role.CLIENT; }

    /** Сотрудник салона (менеджер или администратор). */
    public boolean isStaff()   { return isManager() || isAdmin(); }

    /** Блокировка учётной записи. */
    public void deactivate() { this.active = false; }

    /** Разблокировка учётной записи. */
    public void activate()   { this.active = true; }

    /** Повышение уровня лояльности на одну ступень. */
    public void promoteLoyalty() {
        LoyaltyLevel[] levels = LoyaltyLevel.values();
        int next = Math.min(loyaltyLevel.ordinal() + 1, levels.length - 1);
        this.loyaltyLevel = levels[next];
    }
}
