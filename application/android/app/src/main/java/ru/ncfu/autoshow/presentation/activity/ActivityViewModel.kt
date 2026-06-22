package ru.ncfu.autoshow.presentation.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.OrderDto
import ru.ncfu.autoshow.data.remote.dto.TestDriveDto
import ru.ncfu.autoshow.data.repository.OrderRepository
import ru.ncfu.autoshow.data.repository.TestDriveRepository

/** ViewModel экрана «Мои заявки»: тест-драйвы и заказы клиента. */
class ActivityViewModel(
    private val testDriveRepo: TestDriveRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    var testDrives by mutableStateOf<UiState<List<TestDriveDto>>>(UiState.Loading)
        private set
    var orders by mutableStateOf<UiState<List<OrderDto>>>(UiState.Loading)
        private set

    fun load() {
        loadTestDrives()
        loadOrders()
    }

    private fun loadTestDrives() {
        viewModelScope.launch {
            testDrives = UiState.Loading
            testDriveRepo.getMine()
                .onSuccess { testDrives = UiState.Success(it) }
                .onFailure { testDrives = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            orders = UiState.Loading
            orderRepo.getMine()
                .onSuccess { orders = UiState.Success(it) }
                .onFailure { orders = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun cancelTestDrive(id: Long) {
        viewModelScope.launch { testDriveRepo.cancel(id).onSuccess { loadTestDrives() } }
    }

    fun cancelOrder(id: Long) {
        viewModelScope.launch { orderRepo.cancel(id).onSuccess { loadOrders() } }
    }
}
