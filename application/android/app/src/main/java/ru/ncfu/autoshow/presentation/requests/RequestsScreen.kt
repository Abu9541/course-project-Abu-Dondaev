package ru.ncfu.autoshow.presentation.requests

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
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.core.Labels
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.OrderDto
import ru.ncfu.autoshow.data.remote.dto.TestDriveDto
import ru.ncfu.autoshow.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(vm: RequestsViewModel) {
    LaunchedEffect(Unit) { vm.load() }
    var tab by remember { mutableIntStateOf(0) }
    var tdStatus by remember { mutableStateOf<String?>(null) }
    var orderStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Обработка заявок") }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Тест-драйвы") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Заказы") })
            }
            if (tab == 0) {
                FilterDropdown(
                    label = "Статус заявки",
                    allLabel = "Все статусы",
                    options = Labels.testDriveStatuses,
                    selected = tdStatus,
                    onSelect = { tdStatus = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
                when (val s = vm.testDrives) {
                    is UiState.Loading -> FullScreenLoading()
                    is UiState.Error -> FullScreenError(s.message, onRetry = vm::load)
                    is UiState.Success -> {
                        val filtered = s.data.filter { tdStatus == null || it.status == tdStatus }
                        if (filtered.isEmpty()) FullScreenEmpty("Нет заявок на тест-драйв", icon = Icons.Outlined.Inbox)
                        else LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filtered, key = { it.id }) { td -> StaffTestDriveCard(td, vm) }
                        }
                    }
                }
            } else {
                FilterDropdown(
                    label = "Статус заказа",
                    allLabel = "Все статусы",
                    options = Labels.orderStatuses,
                    selected = orderStatus,
                    onSelect = { orderStatus = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
                when (val s = vm.orders) {
                    is UiState.Loading -> FullScreenLoading()
                    is UiState.Error -> FullScreenError(s.message, onRetry = vm::load)
                    is UiState.Success -> {
                        val filtered = s.data.filter { orderStatus == null || it.status == orderStatus }
                        if (filtered.isEmpty()) FullScreenEmpty("Нет заказов", icon = Icons.Outlined.Inbox)
                        else LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filtered, key = { it.id }) { o -> StaffOrderCard(o, vm) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffTestDriveCard(td: TestDriveDto, vm: RequestsViewModel) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(td.vehicleName ?: "Авто", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                StatusBadge(Labels.testDriveStatus(td.status), statusColor(td.status))
            }
            Spacer(Modifier.height(8.dp))
            InfoRow("Клиент", td.userName ?: "—")
            InfoRow("Телефон", td.contactPhone)
            InfoRow("Центр", td.dealerCenter)
            InfoRow("Дата", Format.dateTime(td.scheduledAt))
            td.notes?.takeIf { it.isNotBlank() }?.let { InfoRow("Комментарий", it) }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (td.status) {
                    "PENDING" -> {
                        Button(onClick = { vm.confirmTestDrive(td.id) }) { Text("Подтвердить") }
                        OutlinedButton(onClick = { vm.rejectTestDrive(td.id) }) { Text("Отклонить") }
                    }
                    "CONFIRMED" -> Button(onClick = { vm.completeTestDrive(td.id) }) { Text("Завершить") }
                }
            }
        }
    }
}

@Composable
private fun StaffOrderCard(o: OrderDto, vm: RequestsViewModel) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(o.vehicleName ?: "Авто", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                StatusBadge(Labels.orderStatus(o.status), statusColor(o.status))
            }
            Spacer(Modifier.height(8.dp))
            InfoRow("Клиент", o.userName ?: "—")
            InfoRow("Стоимость", Format.price(o.totalPrice))
            InfoRow("Оплата", Labels.paymentType(o.paymentType))
            o.installmentPlan?.let { InfoRow("Платёж/мес", Format.price(it.monthlyPayment)) }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (o.status) {
                    "PENDING" -> {
                        Button(onClick = { vm.confirmOrder(o.id) }) { Text("Подтвердить") }
                        OutlinedButton(onClick = { vm.cancelOrder(o.id) }) { Text("Отменить") }
                    }
                    "CONFIRMED" -> {
                        Button(onClick = { vm.payOrder(o.id) }) { Text("Оплата") }
                        OutlinedButton(onClick = { vm.cancelOrder(o.id) }) { Text("Отменить") }
                    }
                    "PAID" -> {
                        Button(onClick = { vm.completeOrder(o.id) }) { Text("Завершить") }
                        OutlinedButton(onClick = { vm.cancelOrder(o.id) }) { Text("Отменить") }
                    }
                }
            }
        }
    }
}
