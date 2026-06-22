package ru.ncfu.autoshow.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

/** Режим оформления приложения. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

private val Context.settingsDataStore by preferencesDataStore(name = "autoshow_settings")

/** Пользовательские настройки приложения (тема и т.п.), сохраняемые в DataStore. */
class SettingsManager(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
    }

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    /** Загрузка сохранённых настроек при старте приложения. */
    suspend fun load() {
        val p = context.settingsDataStore.data.first()
        _themeMode.value = p[Keys.THEME]
            ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.SYSTEM
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[Keys.THEME] = mode.name }
        _themeMode.value = mode
    }
}
