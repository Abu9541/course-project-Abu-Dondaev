package ru.ncfu.autoshow.core

/** Обобщённое состояние экрана: загрузка / успех / ошибка. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
