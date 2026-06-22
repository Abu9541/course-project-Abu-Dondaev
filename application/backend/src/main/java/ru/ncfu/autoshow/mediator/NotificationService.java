package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.notification.NotificationResponse;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.enums.NotificationType;

import java.util.List;

/** Mediator: уведомления пользователей. */
public interface NotificationService {

    List<NotificationResponse> getMine(Long userId);

    long unreadCount(Long userId);

    void markRead(Long id, Long userId);

    void markAllRead(Long userId);

    /** Внутренний метод создания уведомления (вызывается из других сервисов). */
    void notify(User user, String title, String message, NotificationType type);

    /**
     * Уведомить всех менеджеров (Role.MANAGER) — например, о новой заявке клиента.
     * Вызывается из других сервисов, чтобы персонал видел поступающие обращения.
     */
    void notifyManagers(String title, String message, NotificationType type);
}
