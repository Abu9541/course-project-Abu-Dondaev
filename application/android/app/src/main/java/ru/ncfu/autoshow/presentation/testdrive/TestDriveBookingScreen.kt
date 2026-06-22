package ru.ncfu.autoshow.presentation.testdrive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Format
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDriveBookingScreen(
    vm: TestDriveBookingViewModel,
    onBack: () -> Unit,
    onBooked: () -> Unit
) {
    var dealerExpanded by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var submitAttempted by remember { mutableStateOf(false) }

    val today = remember { LocalDate.now() }
    val now = remember { LocalDateTime.now() }

    val selectedDate: LocalDate? = remember(selectedDateMillis) {
        selectedDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
    }
    val scheduledAt: LocalDateTime? = remember(selectedDate, selectedHour) {
        val date = selectedDate
        val hour = selectedHour
        if (date != null && hour != null) date.atTime(hour, 0) else null
    }

    // Если дата сменилась на сегодня, а ранее выбранный час уже прошёл — сбрасываем выбор времени.
    LaunchedEffect(selectedDate) {
        val hour = selectedHour
        if (hour != null && selectedDate == today && hour <= now.hour) selectedHour = null
    }

    // -------------------- Валидация (моментальная) --------------------
    val phoneValid = vm.contactPhone.isNotBlank() &&
        TestDriveBookingViewModel.PHONE_REGEX.matches(vm.contactPhone.trim())
    val scheduledInFuture = scheduledAt != null && scheduledAt.isAfter(now)
    val formValid = selectedDateMillis != null && selectedHour != null && scheduledInFuture && phoneValid

    val dateError = if (submitAttempted && selectedDateMillis == null) "Выберите дату тест-драйва" else null
    val timeError = when {
        submitAttempted && selectedHour == null -> "Выберите время"
        selectedHour != null && !scheduledInFuture -> "Это время уже прошло"
        else -> null
    }
    val phoneError = when {
        vm.contactPhone.isBlank() -> if (submitAttempted) "Укажите контактный телефон" else null
        !TestDriveBookingViewModel.PHONE_REGEX.matches(vm.contactPhone.trim()) ->
            "Некорректный номер (10–15 цифр, можно с «+»)"
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Запись на тест-драйв") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (vm.vehicleName.isNotBlank()) {
                Text(vm.vehicleName, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
            }

            // Дилерский центр
            ExposedDropdownMenuBox(expanded = dealerExpanded, onExpandedChange = { dealerExpanded = it }) {
                OutlinedTextField(
                    value = vm.dealerCenter,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Дилерский центр") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dealerExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = dealerExpanded, onDismissRequest = { dealerExpanded = false }) {
                    TestDriveBookingViewModel.DEALER_CENTERS.forEach { center ->
                        DropdownMenuItem(text = { Text(center) }, onClick = {
                            vm.dealerCenter = center; dealerExpanded = false
                        })
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Дата
            OutlinedTextField(
                value = selectedDate?.let { Format.date(it.atStartOfDay().toString()) } ?: "",
                onValueChange = {},
                readOnly = true,
                isError = dateError != null,
                supportingText = if (dateError != null) { { Text(dateError) } } else null,
                label = { Text("Дата тест-драйва") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Filled.CalendarMonth, "Выбрать дату") }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Text("Время", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TestDriveBookingViewModel.TIME_SLOTS.forEach { hour ->
                    val slotInPast = selectedDate == today && hour <= now.hour
                    FilterChip(
                        selected = selectedHour == hour,
                        enabled = !slotInPast,
                        onClick = { selectedHour = hour },
                        label = { Text("%02d:00".format(hour)) }
                    )
                }
            }
            if (timeError != null) {
                Spacer(Modifier.height(4.dp))
                Text(timeError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.contactPhone,
                onValueChange = { vm.contactPhone = it; vm.clearError() },
                label = { Text("Контактный телефон") },
                singleLine = true,
                isError = phoneError != null,
                supportingText = if (phoneError != null) { { Text(phoneError) } } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.notes,
                onValueChange = { vm.notes = it },
                label = { Text("Комментарий (необязательно)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            // Сообщение об ошибке сервера (валидация полей разруливается inline выше).
            if (vm.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(vm.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    submitAttempted = true
                    if (formValid) vm.book(scheduledAt, onBooked)
                },
                enabled = !vm.submitting,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (vm.submitting) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text("Записаться")
            }
        }
    }

    if (showDatePicker) {
        val dpState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis ?: (System.currentTimeMillis() + 86_400_000L),
            selectableDates = remember {
                object : SelectableDates {
                    // Запрещаем выбор прошедших дат прямо в календаре.
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                        return !date.isBefore(LocalDate.now())
                    }
                    override fun isSelectableYear(year: Int): Boolean = year >= LocalDate.now().year
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = dpState.selectedDateMillis
                    showDatePicker = false
                }) { Text("ОК") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Отмена") } }
        ) { DatePicker(state = dpState) }
    }
}
