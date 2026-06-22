package ru.ncfu.autoshow.presentation.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Labels
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    vm: CatalogViewModel,
    isStaff: Boolean,
    onVehicleClick: (Long) -> Unit,
    onAddVehicle: () -> Unit,
    onShowSold: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Каталог автомобилей") },
                actions = {
                    if (onShowSold != null) {
                        IconButton(onClick = onShowSold) {
                            Icon(Icons.Outlined.Sell, "Проданные автомобили")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isStaff) {
                ExtendedFloatingActionButton(
                    onClick = onAddVehicle,
                    icon = { Icon(Icons.Filled.Add, null) },
                    text = { Text("Авто") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            OutlinedTextField(
                value = vm.query,
                onValueChange = vm::onQueryChange,
                placeholder = { Text("Поиск по марке или модели") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { vm.applySearch() }),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Фильтры по марке и типу кузова — выпадающие списки, расположенные рядом
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterDropdown(
                    label = "Марка",
                    allLabel = "Все марки",
                    options = vm.brands.map { it.id to it.name },
                    selected = vm.selectedBrandId,
                    onSelect = { vm.selectBrand(it) },
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    label = "Тип кузова",
                    allLabel = "Все типы",
                    options = Labels.bodyTypes,
                    selected = vm.selectedBodyType,
                    onSelect = { vm.selectBodyType(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Сброс фильтров (показывается, когда что-то выбрано)
            if (vm.query.isNotBlank() || vm.selectedBrandId != null || vm.selectedBodyType != null) {
                TextButton(onClick = { vm.clearFilters() }, modifier = Modifier.padding(start = 8.dp)) {
                    Icon(Icons.Filled.Close, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Сбросить фильтры")
                }
            } else {
                Spacer(Modifier.height(8.dp))
            }

            if (vm.offline) {
                Spacer(Modifier.height(8.dp))
                OfflineBanner()
            }

            when (val s = vm.state) {
                is UiState.Loading -> FullScreenLoading()
                is UiState.Error -> FullScreenError(s.message, onRetry = vm::load)
                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        FullScreenEmpty("По вашему запросу автомобилей не найдено")
                    } else {
                        LazyColumn(
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
    }
}
