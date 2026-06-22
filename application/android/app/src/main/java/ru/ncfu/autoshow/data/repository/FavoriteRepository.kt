package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.MessageDto
import ru.ncfu.autoshow.data.remote.dto.VehicleSummaryDto

/** Репозиторий избранного. */
class FavoriteRepository(private val api: ApiService) {

    suspend fun getMine(): Result<List<VehicleSummaryDto>> = safeApiCall { api.favorites() }

    suspend fun add(vehicleId: Long): Result<MessageDto> = safeApiCall { api.addFavorite(vehicleId) }

    suspend fun remove(vehicleId: Long): Result<MessageDto> = safeApiCall { api.removeFavorite(vehicleId) }
}
