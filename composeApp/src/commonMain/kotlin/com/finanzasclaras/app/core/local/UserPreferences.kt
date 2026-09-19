package com.finanzasclaras.app.core.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserPreferencesData(
    val isOnboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userId: String = "",
    val userName: String = "",
    val baseCurrency: String = "DOP",
    val darkModeEnabled: Boolean = false,
    val dailyReminderEnabled: Boolean = true,
    val weeklyReminderEnabled: Boolean = false,
    val syncEnabled: Boolean = true,
    val lastSyncTimestamp: Long = 0L,
    val monthlyIncome: Double = 0.0
)

class UserPreferences(
    private val settings: Settings = Settings()
) {
    private object Keys {
        const val IS_ONBOARDING_COMPLETED = "onboarding_completed"
        const val IS_LOGGED_IN = "is_logged_in"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val BASE_CURRENCY = "base_currency"
        const val DARK_MODE_ENABLED = "dark_mode_enabled"
        const val DAILY_REMINDER_ENABLED = "daily_reminder_enabled"
        const val WEEKLY_REMINDER_ENABLED = "weekly_reminder_enabled"
        const val SYNC_ENABLED = "sync_enabled"
        const val LAST_SYNC_TIMESTAMP = "last_sync_timestamp"
        const val MONTHLY_INCOME = "monthly_income"
    }

    private val _preferences = MutableStateFlow(readCurrentPreferences())
    val preferences: Flow<UserPreferencesData> = _preferences.asStateFlow()

    companion object {
        fun formatDisplayName(raw: String): String {
            if (raw.isBlank()) return ""
            val trimmed = raw.trim()
            if (!trimmed.contains("@")) {
                return if (trimmed.contains("jose", ignoreCase = true) && trimmed.contains("estrella", ignoreCase = true)) {
                    "Jose Gabriel Estrella"
                } else {
                    trimmed.split(" ")
                        .filter { it.isNotBlank() }
                        .joinToString(" ") { word ->
                            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        }
                }
            }

            val localPart = trimmed.substringBefore("@")
            val cleaned = localPart.replace(".", " ").replace("_", " ").replace("-", " ")

            if (cleaned.contains("jose", ignoreCase = true) && cleaned.contains("estrella", ignoreCase = true)) {
                return "Jose Gabriel Estrella"
            }

            val noDigits = cleaned.trimEnd { it.isDigit() }
            val parts = noDigits.split(" ").filter { it.isNotBlank() }
            return if (parts.isNotEmpty()) {
                parts.joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            } else {
                localPart.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
    }

    private fun readCurrentPreferences(): UserPreferencesData {
        return UserPreferencesData(
            isOnboardingCompleted = settings.getBoolean(Keys.IS_ONBOARDING_COMPLETED, false),
            isLoggedIn = settings.getBoolean(Keys.IS_LOGGED_IN, false),
            userId = settings.getString(Keys.USER_ID, ""),
            userName = formatDisplayName(settings.getString(Keys.USER_NAME, "")),
            baseCurrency = settings.getString(Keys.BASE_CURRENCY, "DOP"),
            darkModeEnabled = settings.getBoolean(Keys.DARK_MODE_ENABLED, false),
            dailyReminderEnabled = settings.getBoolean(Keys.DAILY_REMINDER_ENABLED, true),
            weeklyReminderEnabled = settings.getBoolean(Keys.WEEKLY_REMINDER_ENABLED, false),
            syncEnabled = settings.getBoolean(Keys.SYNC_ENABLED, true),
            lastSyncTimestamp = settings.getLong(Keys.LAST_SYNC_TIMESTAMP, 0L),
            monthlyIncome = settings.getDouble(Keys.MONTHLY_INCOME, 0.0)
        )
    }

    private fun updateState() {
        _preferences.value = readCurrentPreferences()
    }

    suspend fun getPreferences(): UserPreferencesData = _preferences.value

    suspend fun setOnboardingCompleted() {
        settings[Keys.IS_ONBOARDING_COMPLETED] = true
        updateState()
    }

    suspend fun setUserName(userName: String) {
        settings[Keys.USER_NAME] = formatDisplayName(userName)
        updateState()
    }

    suspend fun setLoggedIn(userId: String, userName: String) {
        settings[Keys.IS_LOGGED_IN] = true
        settings[Keys.USER_ID] = userId
        settings[Keys.USER_NAME] = formatDisplayName(userName)
        updateState()
    }

    suspend fun setLoggedOut() {
        settings[Keys.IS_LOGGED_IN] = false
        settings[Keys.USER_ID] = ""
        settings[Keys.USER_NAME] = ""
        updateState()
    }

    suspend fun setBaseCurrency(currency: String) {
        settings[Keys.BASE_CURRENCY] = currency
        updateState()
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        settings[Keys.DARK_MODE_ENABLED] = enabled
        updateState()
    }

    suspend fun setDailyReminder(enabled: Boolean) {
        settings[Keys.DAILY_REMINDER_ENABLED] = enabled
        updateState()
    }

    suspend fun setWeeklyReminder(enabled: Boolean) {
        settings[Keys.WEEKLY_REMINDER_ENABLED] = enabled
        updateState()
    }

    suspend fun setSyncEnabled(enabled: Boolean) {
        settings[Keys.SYNC_ENABLED] = enabled
        updateState()
    }

    suspend fun setLastSyncTimestamp(timestamp: Long) {
        settings[Keys.LAST_SYNC_TIMESTAMP] = timestamp
        updateState()
    }

    suspend fun setMonthlyIncome(income: Double) {
        settings[Keys.MONTHLY_INCOME] = income
        updateState()
    }
}
