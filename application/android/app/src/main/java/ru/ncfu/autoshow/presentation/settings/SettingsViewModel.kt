package ru.ncfu.autoshow.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.ncfu.autoshow.data.settings.SettingsManager
import ru.ncfu.autoshow.data.settings.ThemeMode

/** ViewModel экрана настроек: управление темой оформления. */
class SettingsViewModel(private val settings: SettingsManager) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settings.themeMode

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settings.setThemeMode(mode) }
    }
}
