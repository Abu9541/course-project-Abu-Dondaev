package ru.ncfu.autoshow.presentation.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.ui.components.FullScreenError
import ru.ncfu.autoshow.ui.components.FullScreenLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(vm: PaymentViewModel, onBack: () -> Unit, onPaid: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Оплата заказа") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } }
            )
        }
    ) { padding ->
        when {
            vm.loading -> FullScreenLoading(Modifier.padding(padding))
            vm.loadError != null -> FullScreenError(vm.loadError!!, Modifier.padding(padding))
            vm.succeeded -> PaymentSuccess(vm.amount, Modifier.padding(padding), onPaid)
            else -> PaymentForm(vm, Modifier.padding(padding))
        }
    }
}

@Composable
private fun PaymentSuccess(amount: Double?, modifier: Modifier, onDone: () -> Unit) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.CheckCircle, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("Оплата прошла успешно", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "Оплачено: ${Format.price(amount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Готово") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentForm(vm: PaymentViewModel, modifier: Modifier) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("К оплате", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            Format.price(vm.amount),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = vm.cardNumber,
            onValueChange = { input ->
                vm.cardNumber = input.filter { it.isDigit() }.take(19).chunked(4).joinToString(" ")
            },
            label = { Text("Номер карты") },
            placeholder = { Text("0000 0000 0000 0000") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = vm.expiry,
                onValueChange = { input ->
                    val d = input.filter { it.isDigit() }.take(4)
                    vm.expiry = if (d.length > 2) "${d.take(2)}/${d.drop(2)}" else d
                },
                label = { Text("ММ/ГГ") },
                placeholder = { Text("12/27") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = vm.cvc,
                onValueChange = { vm.cvc = it.filter { c -> c.isDigit() }.take(4) },
                label = { Text("CVC") },
                placeholder = { Text("123") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = vm.cardHolder,
            onValueChange = { vm.cardHolder = it },
            label = { Text("Держатель карты") },
            placeholder = { Text("IVAN IVANOV") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (vm.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(vm.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { vm.pay() },
            enabled = vm.canPay,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (vm.processing) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Оплатить ${Format.price(vm.amount)}")
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Демо-оплата: реальные деньги не списываются. Тестовая карта отказа — 4000 0000 0000 0002.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
