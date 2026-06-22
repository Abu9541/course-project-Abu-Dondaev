package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.local.CachedVehicleEntity
import ru.ncfu.autoshow.data.local.VehicleDao
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.*

/** Репозиторий каталога: автомобили, марки, оффлайн-кэш карточек. */
class CatalogRepository(
    private val api: ApiService,
    private val vehicleDao: VehicleDao
) {
    suspend fun getVehicles(
        q: String?, brandId: Long?, bodyType: String?, status: String?,
        minPrice: Double?, maxPrice: Double?, page: Int, size: Int,
        sortBy: String, direction: String
    ): Result<PageDto<VehicleSummaryDto>> {
        val result = safeApiCall {
            api.vehicles(q, brandId, bodyType, status, minPrice, maxPrice, page, size, sortBy, direction)
        }
        // Кэшируем первую страницу без фильтров для оффлайн-режима
        val noFilters = q == null && brandId == null && bodyType == null &&
                status == null && minPrice == null && maxPrice == null
        if (result.isSuccess && page == 0 && noFilters) {
            runCatching {
                vehicleDao.replaceAll(result.getOrThrow().content.map { CachedVehicleEntity.from(it) })
            }
        }
        return result
    }

    suspend fun getCachedVehicles(): List<VehicleSummaryDto> =
        runCatching { vehicleDao.getAll().map { it.toDto() } }.getOrDefault(emptyList())

    suspend fun getSoldVehicles(): Result<PageDto<VehicleSummaryDto>> =
        safeApiCall { api.soldVehicles(0, 100) }

    suspend fun getVehicle(id: Long): Result<VehicleDto> = safeApiCall { api.vehicle(id) }

    suspend fun getBrands(): Result<List<BrandDto>> = safeApiCall { api.brands() }

    suspend fun createVehicle(body: VehicleRequestDto): Result<VehicleDto> =
        safeApiCall { api.createVehicle(body) }

    suspend fun updateVehicle(id: Long, body: VehicleRequestDto): Result<VehicleDto> =
        safeApiCall { api.updateVehicle(id, body) }

    suspend fun deleteVehicle(id: Long): Result<MessageDto> = safeApiCall { api.deleteVehicle(id) }
}
