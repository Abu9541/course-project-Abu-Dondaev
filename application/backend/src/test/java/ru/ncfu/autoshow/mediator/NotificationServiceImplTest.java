package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.autoshow.entity.Notification;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.enums.NotificationType;
import ru.ncfu.autoshow.entity.enums.Role;
import ru.ncfu.autoshow.exception.AccessForbiddenException;
import ru.ncfu.autoshow.foundation.NotificationRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.mapper.NotificationMapper;
import ru.ncfu.autoshow.mediator.impl.NotificationServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис уведомлений")
class NotificationServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @Mock UserRepository userRepository;

    NotificationServiceImpl service;

    private NotificationServiceImpl service() {
        return new NotificationServiceImpl(notificationRepository, new NotificationMapper(), userRepository);
    }

    private User user(Long id, Role role) {
        User u = new User();
        u.setId(id);
        u.setRole(role);
        return u;
    }

    @Test
    @DisplayName("notifyManagers создаёт уведомление каждому менеджеру")
    void notifyManagersNotifiesAll() {
        when(userRepository.findByRole(Role.MANAGER))
                .thenReturn(List.of(user(2L, Role.MANAGER), user(3L, Role.MANAGER)));

        service().notifyManagers("Новый заказ", "сообщение", NotificationType.ORDER);

        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    @DisplayName("unreadCount возвращает число непрочитанных")
    void unreadCountReturnsValue() {
        when(notificationRepository.countByUserIdAndReadFalse(4L)).thenReturn(3L);
        assertEquals(3L, service().unreadCount(4L));
    }

    @Test
    @DisplayName("markRead на чужом уведомлении запрещён (403)")
    void markReadForeignForbidden() {
        Notification n = new Notification(user(4L, Role.CLIENT), "t", "m", NotificationType.INFO);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        assertThrows(AccessForbiddenException.class, () -> service().markRead(1L, 5L));
    }

    @Test
    @DisplayName("markRead владельцем помечает уведомление прочитанным")
    void markReadByOwner() {
        Notification n = new Notification(user(4L, Role.CLIENT), "t", "m", NotificationType.INFO);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        service().markRead(1L, 4L);

        assertTrue(n.isRead());
    }
}
