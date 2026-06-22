package ru.ncfu.autoshow.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.DashboardDto
import ru.ncfu.autoshow.ui.components.FullScreenError
import ru.ncfu.autoshow.ui.components.FullScreenLoading
import ru.ncfu.autoshow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: DashboardViewModel) {
    LaunchedEffect(Unit) { vm.load() }

    Scaffold(topBar = { TopAppBar(title = { Text("Аналитика") }) }) { padding ->
        when (val s = vm.state) {
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(s.message, Modifier.padding(padding), onRetry = vm::load)
            is UiState.Success -> DashboardGrid(s.data, Modifier.padding(padding))
        }
    }
}

@Composable
private fun DashboardGrid(d: DashboardDto, modifier: Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            StatCard("Выручка (завершённые сделки)", Format.price(d.totalRevenue), TealTertiary, big = true)
        }
        item { StatCard("Всего авто", d.totalVehicles.toString(), BluePrimary) }
        item { StatCard("В наличии", d.inStock.toString(), StatusGreen) }
        item { StatCard("Забронировано", d.reserved.toString(), StatusAmber) }
        item { StatCard("Продано", d.sold.toString(), StatusGray) }
        item { StatCard("Заказы всего", d.totalOrders.toString(), BluePrimary) }
        item { StatCard("Ожидают обработки", d.pendingOrders.toString(), StatusAmber) }
        item { StatCard("Завершено сделок", d.completedOrders.toString(), StatusGreen) }
        item { StatCard("Тест-драйвы в ожидании", d.pendingTestDrives.toString(), StatusAmber) }
        item(span = { GridItemSpan(2) }) {
            StatCard("Пользователей в системе", d.totalUsers.toString(), SlateSecondary)
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, accent: Color, big: Boolean = false) {
    Card(colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f))) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                value,
                style = if (big) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = accent
            )
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
