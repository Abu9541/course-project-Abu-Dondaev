package ru.ncfu.autoshow.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ncfu.autoshow.entity.enums.LoyaltyLevel;
import ru.ncfu.autoshow.entity.enums.Role;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сущность User (роли и бизнес-методы)")
class UserTest {

    @Test
    @DisplayName("Признаки ролей: admin/manager/client/staff")
    void roleFlags() {
        User u = new User();

        u.setRole(Role.ADMIN);
        assertTrue(u.isAdmin());
        assertTrue(u.isStaff());
        assertFalse(u.isClient());

        u.setRole(Role.MANAGER);
        assertTrue(u.isManager());
        assertTrue(u.isStaff());

        u.setRole(Role.CLIENT);
        assertTrue(u.isClient());
        assertFalse(u.isStaff());
    }

    @Test
    @DisplayName("Блокировка и разблокировка учётной записи")
    void activation() {
        User u = new User();
        u.deactivate();
        assertFalse(u.isActive());
        u.activate();
        assertTrue(u.isActive());
    }

    @Test
    @DisplayName("Повышение лояльности на ступень с ограничением сверху")
    void promoteLoyalty() {
        User u = new User();
        u.setLoyaltyLevel(LoyaltyLevel.STANDARD);

        u.promoteLoyalty();
        assertEquals(LoyaltyLevel.SILVER, u.getLoyaltyLevel());
        u.promoteLoyalty();
        assertEquals(LoyaltyLevel.GOLD, u.getLoyaltyLevel());
        u.promoteLoyalty();
        assertEquals(LoyaltyLevel.PLATINUM, u.getLoyaltyLevel());

        u.promoteLoyalty(); // выше PLATINUM не поднимается
        assertEquals(LoyaltyLevel.PLATINUM, u.getLoyaltyLevel());
    }
}
