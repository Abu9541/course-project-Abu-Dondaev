package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.DashboardDto

/** Репозиторий сводной аналитики. */
class DashboardRepository(private val api: ApiService) {
    suspend fun getStats(): Result<DashboardDto> = safeApiCall { api.dashboard() }
}
