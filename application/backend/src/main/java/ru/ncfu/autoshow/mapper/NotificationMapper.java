package ru.ncfu.autoshow.mapper;

import org.springframework.stereotype.Component;
import ru.ncfu.autoshow.dto.notification.NotificationResponse;
import ru.ncfu.autoshow.entity.Notification;

/** Data Mapper для уведомлений. */
@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
