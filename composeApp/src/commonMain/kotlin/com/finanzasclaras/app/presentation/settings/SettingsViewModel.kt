package com.finanzasclaras.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
    val baseCurrency: String = "DOP",
    val darkModeEnabled: Boolean = false,
    val dailyReminder: Boolean = true,
    val weeklyReminder: Boolean = false,
    val syncEnabled: Boolean = true,
    val isLoggedIn: Boolean = false,
    val userName: String = ""
)

class SettingsViewModel constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val prefs = userPreferences.preferences.first()
            _state.value = SettingsUiState(
                baseCurrency = prefs.baseCurrency,
                darkModeEnabled = prefs.darkModeEnabled,
                dailyReminder = prefs.dailyReminderEnabled,
                weeklyReminder = prefs.weeklyReminderEnabled,
                syncEnabled = prefs.syncEnabled,
                isLoggedIn = prefs.isLoggedIn,
                userName = prefs.userName
            )
        }
    }

    fun setBaseCurrency(currency: String) {
        viewModelScope.launch {
            userPreferences.setBaseCurrency(currency)
            _state.value = _state.value.copy(baseCurrency = currency)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkModeEnabled(enabled)
            _state.value = _state.value.copy(darkModeEnabled = enabled)
        }
    }

    fun setDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDailyReminder(enabled)
            _state.value = _state.value.copy(dailyReminder = enabled)
        }
    }

    fun setWeeklyReminder(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setWeeklyReminder(enabled)
            _state.value = _state.value.copy(weeklyReminder = enabled)
        }
    }

    fun setSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setSyncEnabled(enabled)
            _state.value = _state.value.copy(syncEnabled = enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.setLoggedOut()
        }
    }
}
