package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.notification.NotificationResponse;
import ru.ncfu.autoshow.entity.Notification;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.enums.NotificationType;
import ru.ncfu.autoshow.entity.enums.Role;
import ru.ncfu.autoshow.exception.AccessForbiddenException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.NotificationRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.mapper.NotificationMapper;
import ru.ncfu.autoshow.mediator.NotificationService;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationMapper notificationMapper,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMine(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(notificationMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public void markRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Уведомление", id));
        if (!notification.getUser().getId().equals(userId)) {
            throw new AccessForbiddenException("Уведомление принадлежит другому пользователю");
        }
        notification.markRead();
    }

    @Override
    public void markAllRead(Long userId) {
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .forEach(Notification::markRead);
    }

    @Override
    public void notify(User user, String title, String message, NotificationType type) {
        notificationRepository.save(new Notification(user, title, message, type));
    }

    @Override
    public void notifyManagers(String title, String message, NotificationType type) {
        for (User manager : userRepository.findByRole(Role.MANAGER)) {
            notificationRepository.save(new Notification(manager, title, message, type));
        }
    }
}
