package ru.ncfu.autoshow.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.core.Labels
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.UserDto
import ru.ncfu.autoshow.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: ProfileViewModel, onLoggedOut: () -> Unit, onOpenSettings: () -> Unit) {
    LaunchedEffect(Unit) { vm.load() }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(vm.message) { vm.message?.let { snackbar.showSnackbar(it); vm.consumeMessage() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                actions = {
                    if (vm.state is UiState.Success && !vm.editing) {
                        IconButton(onClick = { vm.startEdit() }) { Icon(Icons.Filled.Edit, "Редактировать") }
                    }
                    IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, "Настройки") }
                }
            )
        }
    ) { padding ->
        when (val s = vm.state) {
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(s.message, Modifier.padding(padding), onRetry = vm::load)
            is UiState.Success -> ProfileContent(
                user = s.data,
                vm = vm,
                onLoggedOut = onLoggedOut,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ProfileContent(user: UserDto, vm: ProfileViewModel, onLoggedOut: () -> Unit, modifier: Modifier) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(88.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, null, Modifier.size(44.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))
        Text(user.fullName, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusBadge(Labels.role(user.role), MaterialTheme.colorScheme.primary)
            StatusBadge(Labels.loyalty(user.loyaltyLevel), MaterialTheme.colorScheme.tertiary)
        }

        Spacer(Modifier.height(24.dp))
        if (vm.editing) {
            OutlinedTextField(
                value = vm.fullName, onValueChange = { vm.fullName = it },
                label = { Text("ФИО") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.phone, onValueChange = { vm.phone = it },
                label = { Text("Телефон") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { vm.cancelEdit() }, modifier = Modifier.weight(1f)) { Text("Отмена") }
                Button(onClick = { vm.save() }, enabled = !vm.saving, modifier = Modifier.weight(1f)) {
                    if (vm.saving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Сохранить")
                }
            }
        } else {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow("Email", user.email)
                    InfoRow("Телефон", user.phone ?: "—")
                    InfoRow("Роль", Labels.role(user.role))
                    InfoRow("Статус", if (user.active) "Активен" else "Заблокирован")
                    InfoRow("Регистрация", Format.date(user.createdAt))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = { vm.logout(onLoggedOut) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, null); Spacer(Modifier.width(8.dp)); Text("Выйти из аккаунта")
        }
    }
}
