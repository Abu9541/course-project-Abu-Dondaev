package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.MessageDto
import ru.ncfu.autoshow.data.remote.dto.NotificationDto

/** Репозиторий уведомлений. */
class NotificationRepository(private val api: ApiService) {

    suspend fun getMine(): Result<List<NotificationDto>> = safeApiCall { api.notifications() }

    suspend fun unreadCount(): Result<Long> =
        safeApiCall { api.unreadCount()["count"] ?: 0L }

    suspend fun markRead(id: Long): Result<MessageDto> = safeApiCall { api.markNotificationRead(id) }

    suspend fun markAllRead(): Result<MessageDto> = safeApiCall { api.markAllNotificationsRead() }
}
