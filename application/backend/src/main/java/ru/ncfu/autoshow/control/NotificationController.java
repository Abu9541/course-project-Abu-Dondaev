package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.common.MessageResponse;
import ru.ncfu.autoshow.dto.notification.NotificationResponse;
import ru.ncfu.autoshow.mediator.NotificationService;
import ru.ncfu.autoshow.security.CustomUserDetails;

import java.util.List;
import java.util.Map;

/** Control: уведомления пользователя. */
@Tag(name = "Уведомления", description = "Уведомления пользователя о событиях системы")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Мои уведомления")
    @GetMapping
    public List<NotificationResponse> my(@AuthenticationPrincipal CustomUserDetails principal) {
        return notificationService.getMine(principal.getId());
    }

    @Operation(summary = "Количество непрочитанных уведомлений")
    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal CustomUserDetails principal) {
        return Map.of("count", notificationService.unreadCount(principal.getId()));
    }

    @Operation(summary = "Отметить уведомление прочитанным")
    @PostMapping("/{id}/read")
    public MessageResponse read(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        notificationService.markRead(id, principal.getId());
        return new MessageResponse("Отмечено как прочитанное");
    }

    @Operation(summary = "Отметить все уведомления прочитанными")
    @PostMapping("/read-all")
    public MessageResponse readAll(@AuthenticationPrincipal CustomUserDetails principal) {
        notificationService.markAllRead(principal.getId());
        return new MessageResponse("Все уведомления прочитаны");
    }
}
