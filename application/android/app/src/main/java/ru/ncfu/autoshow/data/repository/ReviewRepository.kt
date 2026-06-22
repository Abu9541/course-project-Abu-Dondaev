package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.ReviewDto
import ru.ncfu.autoshow.data.remote.dto.ReviewRequestDto

/** Репозиторий отзывов. */
class ReviewRepository(private val api: ApiService) {

    suspend fun getByVehicle(vehicleId: Long): Result<List<ReviewDto>> =
        safeApiCall { api.reviews(vehicleId) }

    suspend fun add(vehicleId: Long, rating: Int, comment: String?): Result<ReviewDto> =
        safeApiCall { api.addReview(vehicleId, ReviewRequestDto(rating, comment)) }
}
