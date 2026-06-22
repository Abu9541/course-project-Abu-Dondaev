package ru.ncfu.autoshow.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Labels
import ru.ncfu.autoshow.ui.components.FullScreenLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVehicleScreen(
    vm: EditVehicleViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vm.isEdit) "Редактировать авто" else "Новый автомобиль") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } }
            )
        }
    ) { padding ->
        if (vm.loading) {
            FullScreenLoading(Modifier.padding(padding))
            return@Scaffold
        }
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            LabeledDropdown(
                label = "Марка",
                options = vm.brands.map { it.id.toString() to it.name },
                selectedValue = vm.brandId?.toString(),
                onSelect = { vm.brandId = it.toLongOrNull() }
            )
            Spacer(Modifier.height(10.dp))
            FormField("Модель", vm.model) { vm.model = it; vm.clearError() }
            FormField("VIN (17 символов)", vm.vin) { vm.vin = it; vm.clearError() }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormField("Год", vm.yearText, Modifier.weight(1f), KeyboardType.Number) { vm.yearText = it }
                FormField("Цена, ₽", vm.priceText, Modifier.weight(1f), KeyboardType.Number) { vm.priceText = it }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormField("Мощность, л.с.", vm.powerText, Modifier.weight(1f), KeyboardType.Number) { vm.powerText = it }
                FormField("Пробег, км", vm.mileageText, Modifier.weight(1f), KeyboardType.Number) { vm.mileageText = it }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormField("Объём, л", vm.volumeText, Modifier.weight(1f), KeyboardType.Decimal) { vm.volumeText = it }
                FormField("Расход", vm.fuelText, Modifier.weight(1f), KeyboardType.Decimal) { vm.fuelText = it }
            }
            FormField("Цвет", vm.color) { vm.color = it }
            FormField("Комплектация", vm.equipmentLevel) { vm.equipmentLevel = it }

            Spacer(Modifier.height(10.dp))
            LabeledDropdown("Тип кузова", Labels.bodyTypes, vm.bodyType) { vm.bodyType = it }
            Spacer(Modifier.height(10.dp))
            LabeledDropdown("Двигатель", Labels.engineTypes, vm.engineType) { vm.engineType = it }
            Spacer(Modifier.height(10.dp))
            LabeledDropdown("КПП", Labels.transmissions, vm.transmission) { vm.transmission = it }
            Spacer(Modifier.height(10.dp))
            LabeledDropdown("Привод", Labels.driveTypes, vm.driveType) { vm.driveType = it }
            Spacer(Modifier.height(10.dp))
            LabeledDropdown("Статус", Labels.vehicleStatuses, vm.status) { vm.status = it }
            Spacer(Modifier.height(10.dp))

            FormField("Ссылка на изображение", vm.imageUrl) { vm.imageUrl = it }
            OutlinedTextField(
                value = vm.description,
                onValueChange = { vm.description = it },
                label = { Text("Описание") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            if (vm.error != null) {
                Spacer(Modifier.height(10.dp))
                Text(vm.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { vm.save(onSaved) },
                enabled = !vm.saving,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (vm.saving) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text(if (vm.isEdit) "Сохранить" else "Добавить автомобиль")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabeledDropdown(
    label: String,
    options: List<Pair<String, String>>,
    selectedValue: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedValue }?.second ?: ""
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (value, text) ->
                DropdownMenuItem(text = { Text(text) }, onClick = { onSelect(value); expanded = false })
            }
        }
    }
}
