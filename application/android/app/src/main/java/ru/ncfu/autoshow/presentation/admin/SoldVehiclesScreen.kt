package ru.ncfu.autoshow.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.ui.components.FullScreenEmpty
import ru.ncfu.autoshow.ui.components.FullScreenError
import ru.ncfu.autoshow.ui.components.FullScreenLoading
import ru.ncfu.autoshow.ui.components.VehicleCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoldVehiclesScreen(
    vm: SoldVehiclesViewModel,
    onBack: () -> Unit,
    onVehicleClick: (Long) -> Unit
) {
    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Проданные автомобили") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") }
                }
            )
        }
    ) { padding ->
        when (val s = vm.state) {
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(s.message, Modifier.padding(padding), onRetry = vm::load)
            is UiState.Success -> if (s.data.isEmpty()) {
                FullScreenEmpty("Проданных автомобилей пока нет", Modifier.padding(padding), Icons.Outlined.Sell)
            } else {
                LazyColumn(
                    Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(s.data, key = { it.id }) { vehicle ->
                        VehicleCard(vehicle = vehicle, onClick = { onVehicleClick(vehicle.id) })
                    }
                }
            }
        }
    }
}
