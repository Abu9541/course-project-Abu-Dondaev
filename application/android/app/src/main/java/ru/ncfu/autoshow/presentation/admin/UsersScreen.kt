package ru.ncfu.autoshow.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Labels
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.UserDto
import ru.ncfu.autoshow.ui.components.FullScreenError
import ru.ncfu.autoshow.ui.components.FullScreenLoading
import ru.ncfu.autoshow.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(vm: UsersViewModel) {
    LaunchedEffect(Unit) { vm.load() }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(vm.message) { vm.message?.let { snackbar.showSnackbar(it); vm.consumeMessage() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TopAppBar(title = { Text("Пользователи") }) }
    ) { padding ->
        when (val s = vm.state) {
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(s.message, Modifier.padding(padding), onRetry = vm::load)
            is UiState.Success -> LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(s.data, key = { it.id }) { user -> UserCard(user, vm) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserCard(user: UserDto, vm: UsersViewModel) {
    var roleMenu by remember { mutableStateOf(false) }
    val roles = listOf("CLIENT", "MANAGER", "ADMIN")

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(user.fullName, fontWeight = FontWeight.SemiBold)
                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(Labels.role(user.role), MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    OutlinedButton(onClick = { roleMenu = true }) { Text("Роль: ${Labels.role(user.role)}") }
                    DropdownMenu(expanded = roleMenu, onDismissRequest = { roleMenu = false }) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(Labels.role(role)) },
                                onClick = { roleMenu = false; if (role != user.role) vm.updateRole(user.id, role) }
                            )
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(if (user.active) "Активен" else "Заблок.", style = MaterialTheme.typography.bodySmall)
                Switch(checked = user.active, onCheckedChange = { vm.setActive(user.id, it) })
            }
        }
    }
}
