package ru.ncfu.autoshow.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.ncfu.autoshow.data.settings.ThemeMode

private const val SUPPORT_EMAIL = "dondaevabu126@gmail.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel,
    onBack: () -> Unit,
    onOpenUserAgreement: () -> Unit
) {
    val context = LocalContext.current
    val themeMode by vm.themeMode.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {

            SectionTitle("Тема оформления")
            ThemeMode.values().forEach { mode ->
                Row(
                    Modifier.fillMaxWidth()
                        .selectable(selected = themeMode == mode, onClick = { vm.setThemeMode(mode) })
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = themeMode == mode, onClick = { vm.setThemeMode(mode) })
                    Spacer(Modifier.width(12.dp))
                    Text(themeLabel(mode), style = MaterialTheme.typography.bodyLarge)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SectionTitle("Поддержка и информация")

            SettingsRow(Icons.Outlined.Email, "Написать в поддержку", SUPPORT_EMAIL) {
                runCatching {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$SUPPORT_EMAIL")
                        putExtra(Intent.EXTRA_SUBJECT, "Поддержка AutoShow")
                    }
                    context.startActivity(Intent.createChooser(intent, "Написать в поддержку"))
                }
            }
            SettingsRow(Icons.Outlined.Share, "Поделиться приложением", null) {
                runCatching {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Попробуйте приложение AutoShow — каталог автомобилей, тест-драйвы и покупка в рассрочку!"
                        )
                    }
                    context.startActivity(Intent.createChooser(intent, "Поделиться приложением"))
                }
            }
            SettingsRow(Icons.Outlined.Description, "Пользовательское соглашение", null, onClick = onOpenUserAgreement)

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String?, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun themeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> "Системная"
    ThemeMode.LIGHT -> "Светлая"
    ThemeMode.DARK -> "Тёмная"
}
