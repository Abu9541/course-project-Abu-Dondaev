package ru.ncfu.autoshow.presentation.activity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.core.Labels
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.OrderDto
import ru.ncfu.autoshow.data.remote.dto.TestDriveDto
import ru.ncfu.autoshow.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(vm: ActivityViewModel, onPay: (Long) -> Unit) {
    // Перезагружаем список при каждом показе экрана — чтобы статус обновлялся после оплаты.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.load() }
    var tab by remember { mutableIntStateOf(0) }
    var tdStatus by remember { mutableStateOf<String?>(null) }
    var orderStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Мои заявки") }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Тест-драйвы") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Покупки") })
            }
            if (tab == 0) {
                FilterDropdown(
                    label = "Статус", allLabel = "Все статусы",
                    options = Labels.testDriveStatuses, selected = tdStatus, onSelect = { tdStatus = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
                TestDriveList(vm.testDrives, tdStatus, onCancel = vm::cancelTestDrive, onRetry = vm::load)
            } else {
                FilterDropdown(
                    label = "Статус", allLabel = "Все статусы",
                    options = Labels.orderStatuses, selected = orderStatus, onSelect = { orderStatus = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
                OrderList(vm.orders, orderStatus, onCancel = vm::cancelOrder, onPay = onPay, onRetry = vm::load)
            }
        }
    }
}

@Composable
private fun TestDriveList(state: UiState<List<TestDriveDto>>, statusFilter: String?, onCancel: (Long) -> Unit, onRetry: () -> Unit) {
    when (state) {
        is UiState.Loading -> FullScreenLoading()
        is UiState.Error -> FullScreenError(state.message, onRetry = onRetry)
        is UiState.Success -> {
            val data = state.data.filter { statusFilter == null || it.status == statusFilter }
            if (data.isEmpty()) {
                FullScreenEmpty("У вас нет записей на тест-драйв", icon = Icons.Outlined.Inbox)
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(data, key = { it.id }) { td -> ClientTestDriveCard(td, onCancel) }
                }
            }
        }
    }
}

@Composable
private fun OrderList(state: UiState<List<OrderDto>>, statusFilter: String?, onCancel: (Long) -> Unit, onPay: (Long) -> Unit, onRetry: () -> Unit) {
    when (state) {
        is UiState.Loading -> FullScreenLoading()
        is UiState.Error -> FullScreenError(state.message, onRetry = onRetry)
        is UiState.Success -> {
            val data = state.data.filter { statusFilter == null || it.status == statusFilter }
            if (data.isEmpty()) {
                FullScreenEmpty("У вас нет заказов", icon = Icons.Outlined.Inbox)
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(data, key = { it.id }) { o -> ClientOrderCard(o, onCancel, onPay) }
                }
            }
        }
    }
}

@Composable
private fun ClientTestDriveCard(td: TestDriveDto, onCancel: (Long) -> Unit) {
    val active = td.status == "PENDING" || td.status == "CONFIRMED"
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(td.vehicleName ?: "Автомобиль", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                StatusBadge(Labels.testDriveStatus(td.status), statusColor(td.status))
            }
            Spacer(Modifier.height(8.dp))
            InfoRow("Дилерский центр", td.dealerCenter)
            InfoRow("Дата и время", Format.dateTime(td.scheduledAt))
            td.managerName?.let { InfoRow("Менеджер", it) }
            if (active) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { onCancel(td.id) }, modifier = Modifier.align(Alignment.End)) {
                    Text("Отменить")
                }
            }
        }
    }
}

@Composable
private fun ClientOrderCard(o: OrderDto, onCancel: (Long) -> Unit, onPay: (Long) -> Unit) {
    val payable = o.status == "PENDING" || o.status == "CONFIRMED"
    val active = o.status != "COMPLETED" && o.status != "CANCELLED"
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(o.vehicleName ?: "Автомобиль", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                StatusBadge(Labels.orderStatus(o.status), statusColor(o.status))
            }
            Spacer(Modifier.height(8.dp))
            InfoRow("Стоимость", Format.price(o.totalPrice))
            InfoRow("Оплата", Labels.paymentType(o.paymentType))
            o.installmentPlan?.let {
                InfoRow("Ежемесячный платёж", Format.price(it.monthlyPayment))
                InfoRow("Срок", "${it.termMonths} мес.")
            }
            if (active) {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (payable) {
                        Button(onClick = { onPay(o.id) }, modifier = Modifier.weight(1f)) {
                            Text(if (o.paymentType == "INSTALLMENT") "Оплатить взнос" else "Оплатить")
                        }
                    }
                    OutlinedButton(onClick = { onCancel(o.id) }, modifier = Modifier.weight(1f)) {
                        Text("Отменить")
                    }
                }
            }
        }
    }
}
