package ru.ncfu.autoshow.presentation.purchase

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.ui.components.FullScreenError
import ru.ncfu.autoshow.ui.components.FullScreenLoading
import ru.ncfu.autoshow.ui.components.InfoRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    vm: PurchaseViewModel,
    onBack: () -> Unit,
    onPurchased: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Оформление покупки") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } }
            )
        }
    ) { padding ->
        val vehicle = vm.vehicle
        when {
            vm.loadError != null -> FullScreenError(vm.loadError!!, Modifier.padding(padding))
            vehicle == null -> FullScreenLoading(Modifier.padding(padding))
            else -> Column(
                Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
            ) {
                Text("${vehicle.brand?.name.orEmpty()} ${vehicle.model}".trim(), style = MaterialTheme.typography.titleLarge)
                Text(Format.price(vehicle.price), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)

                Spacer(Modifier.height(20.dp))
                Text("Способ оплаты", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = vm.paymentType == "FULL",
                        onClick = { vm.selectPaymentType("FULL") },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) { Text("Полная оплата") }
                    SegmentedButton(
                        selected = vm.paymentType == "INSTALLMENT",
                        onClick = { vm.selectPaymentType("INSTALLMENT") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) { Text("Рассрочка") }
                }

                if (vm.paymentType == "INSTALLMENT") {
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = vm.downPaymentText,
                        onValueChange = { vm.downPaymentText = it; vm.clearError() },
                        label = { Text("Первоначальный взнос, ₽") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = { TextButton(onClick = { vm.recalculate() }) { Text("Расчёт") } },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))
                    Text("Срок рассрочки", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PurchaseViewModel.TERMS.forEach { term ->
                            FilterChip(
                                selected = vm.termMonths == term,
                                onClick = { vm.setTerm(term) },
                                label = { Text("$term мес.") }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    val plan = vm.plan
                    if (vm.calculating) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    } else if (plan != null) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Условия рассрочки", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Spacer(Modifier.height(8.dp))
                                InfoRow("Ежемесячный платёж", Format.price(plan.monthlyPayment))
                                InfoRow("Первоначальный взнос", Format.price(plan.downPayment))
                                InfoRow("Срок", "${plan.termMonths} мес.")
                                InfoRow("Ставка", "${plan.interestRate}% годовых")
                                InfoRow("Переплата", Format.price(plan.overpayment))
                                InfoRow("Итоговая сумма", Format.price(plan.totalAmount))
                            }
                        }
                    }
                }

                if (vm.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(vm.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { vm.buy(onPurchased) },
                    enabled = !vm.submitting,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (vm.submitting) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text(
                        if (vm.paymentType == "INSTALLMENT") "Оформить рассрочку" else "Купить за ${Format.price(vehicle.price)}",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "После оформления вы перейдёте к оплате. Завершение сделки подтверждает менеджер салона.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
