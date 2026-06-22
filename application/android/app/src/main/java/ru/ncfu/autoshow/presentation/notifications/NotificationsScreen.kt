package ru.ncfu.autoshow.presentation.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.NotificationDto
import ru.ncfu.autoshow.ui.components.FullScreenEmpty
import ru.ncfu.autoshow.ui.components.FullScreenError
import ru.ncfu.autoshow.ui.components.FullScreenLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(vm: NotificationsViewModel) {
    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Уведомления") },
                actions = {
                    IconButton(onClick = { vm.markAllRead() }) {
                        Icon(Icons.Outlined.DoneAll, "Прочитать все")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = vm.state) {
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(s.message, Modifier.padding(padding), onRetry = vm::load)
            is UiState.Success -> if (s.data.isEmpty()) {
                FullScreenEmpty("Нет уведомлений", Modifier.padding(padding), Icons.Outlined.Notifications)
            } else {
                LazyColumn(
                    Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(s.data, key = { it.id }) { n ->
                        NotificationCard(n) { if (!n.read) vm.markRead(n.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(n: NotificationDto, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (n.read) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(Modifier.padding(14.dp)) {
            if (!n.read) {
                Box(Modifier.padding(top = 6.dp).size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                Spacer(Modifier.width(10.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(n.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(n.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Text(Format.dateTime(n.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
