package ru.ncfu.autoshow.presentation.requests

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

/** ViewModel обработки заявок персоналом (тест-драйвы и заказы). */
class RequestsViewModel(
    private val testDriveRepo: TestDriveRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    var testDrives by mutableStateOf<UiState<List<TestDriveDto>>>(UiState.Loading)
        private set
    var orders by mutableStateOf<UiState<List<OrderDto>>>(UiState.Loading)
        private set

    fun load() { loadTestDrives(); loadOrders() }

    private fun loadTestDrives() {
        viewModelScope.launch {
            testDrives = UiState.Loading
            testDriveRepo.getAll()
                .onSuccess { testDrives = UiState.Success(it) }
                .onFailure { testDrives = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            orders = UiState.Loading
            orderRepo.getAll()
                .onSuccess { orders = UiState.Success(it) }
                .onFailure { orders = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun confirmTestDrive(id: Long) = actTestDrive { testDriveRepo.confirm(id) }
    fun rejectTestDrive(id: Long) = actTestDrive { testDriveRepo.reject(id) }
    fun completeTestDrive(id: Long) = actTestDrive { testDriveRepo.complete(id) }

    fun confirmOrder(id: Long) = actOrder { orderRepo.confirm(id) }
    fun payOrder(id: Long) = actOrder { orderRepo.pay(id) }
    fun completeOrder(id: Long) = actOrder { orderRepo.complete(id) }
    fun cancelOrder(id: Long) = actOrder { orderRepo.cancel(id) }

    private fun actTestDrive(block: suspend () -> Result<*>) {
        viewModelScope.launch { block().onSuccess { loadTestDrives() } }
    }

    private fun actOrder(block: suspend () -> Result<*>) {
        viewModelScope.launch { block().onSuccess { loadOrders() } }
    }
}
