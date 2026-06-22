package ru.ncfu.autoshow.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.ReviewDto
import ru.ncfu.autoshow.data.remote.dto.VehicleDto
import ru.ncfu.autoshow.data.repository.CatalogRepository
import ru.ncfu.autoshow.data.repository.FavoriteRepository
import ru.ncfu.autoshow.data.repository.ReviewRepository

/** ViewModel карточки автомобиля (детальная информация, отзывы, избранное). */
class VehicleDetailViewModel(
    private val vehicleId: Long,
    private val catalogRepo: CatalogRepository,
    private val favoriteRepo: FavoriteRepository,
    private val reviewRepo: ReviewRepository
) : ViewModel() {

    var state by mutableStateOf<UiState<VehicleDto>>(UiState.Loading)
        private set
    var reviews by mutableStateOf<List<ReviewDto>>(emptyList())
        private set
    var isFavorite by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set
    var reviewSubmitting by mutableStateOf(false)
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            state = UiState.Loading
            catalogRepo.getVehicle(vehicleId)
                .onSuccess { state = UiState.Success(it) }
                .onFailure { state = UiState.Error(it.message ?: "Не удалось загрузить автомобиль") }
            loadReviews()
            refreshFavorite()
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            reviewRepo.getByVehicle(vehicleId).onSuccess { reviews = it }
        }
    }

    private fun refreshFavorite() {
        viewModelScope.launch {
            favoriteRepo.getMine().onSuccess { list -> isFavorite = list.any { it.id == vehicleId } }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val result = if (isFavorite) favoriteRepo.remove(vehicleId) else favoriteRepo.add(vehicleId)
            result.onSuccess {
                isFavorite = !isFavorite
                message = if (isFavorite) "Добавлено в избранное" else "Удалено из избранного"
            }.onFailure { message = it.message }
        }
    }

    fun submitReview(rating: Int, comment: String, onDone: () -> Unit) {
        viewModelScope.launch {
            reviewSubmitting = true
            reviewRepo.add(vehicleId, rating, comment.ifBlank { null })
                .onSuccess {
                    message = "Спасибо за отзыв!"
                    loadReviews()
                    // обновим средний рейтинг
                    catalogRepo.getVehicle(vehicleId).onSuccess { state = UiState.Success(it) }
                    onDone()
                }
                .onFailure { message = it.message }
            reviewSubmitting = false
        }
    }

    fun deleteVehicle(onDeleted: () -> Unit) {
        viewModelScope.launch {
            catalogRepo.deleteVehicle(vehicleId)
                .onSuccess { onDeleted() }
                .onFailure { message = it.message }
        }
    }

    fun consumeMessage() { message = null }
}
