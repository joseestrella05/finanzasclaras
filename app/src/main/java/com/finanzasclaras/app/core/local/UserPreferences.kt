package com.finanzasclaras.app.core.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

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
    val lastSyncTimestamp: Long = 0L
)

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val BASE_CURRENCY = stringPreferencesKey("base_currency")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val WEEKLY_REMINDER_ENABLED = booleanPreferencesKey("weekly_reminder_enabled")
        val SYNC_ENABLED = booleanPreferencesKey("sync_enabled")
        val LAST_SYNC_TIMESTAMP = doublePreferencesKey("last_sync_timestamp")
    }

    val preferences: Flow<UserPreferencesData> = context.dataStore.data.map { prefs ->
        UserPreferencesData(
            isOnboardingCompleted = prefs[Keys.IS_ONBOARDING_COMPLETED] ?: false,
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            userId = prefs[Keys.USER_ID] ?: "",
            userName = prefs[Keys.USER_NAME] ?: "",
            baseCurrency = prefs[Keys.BASE_CURRENCY] ?: "DOP",
            darkModeEnabled = prefs[Keys.DARK_MODE_ENABLED] ?: false,
            dailyReminderEnabled = prefs[Keys.DAILY_REMINDER_ENABLED] ?: true,
            weeklyReminderEnabled = prefs[Keys.WEEKLY_REMINDER_ENABLED] ?: false,
            syncEnabled = prefs[Keys.SYNC_ENABLED] ?: true,
            lastSyncTimestamp = (prefs[Keys.LAST_SYNC_TIMESTAMP] ?: 0L).toLong()
        )
    }

    suspend fun getPreferences(): UserPreferencesData = preferences.first()

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { it[Keys.IS_ONBOARDING_COMPLETED] = true }
    }

    suspend fun setLoggedIn(userId: String, userName: String) {
        context.dataStore.edit {
            it[Keys.IS_LOGGED_IN] = true
            it[Keys.USER_ID] = userId
            it[Keys.USER_NAME] = userName
        }
    }

    suspend fun setLoggedOut() {
        context.dataStore.edit {
            it[Keys.IS_LOGGED_IN] = false
            it[Keys.USER_ID] = ""
            it[Keys.USER_NAME] = ""
        }
    }

    suspend fun setBaseCurrency(currency: String) {
        context.dataStore.edit { it[Keys.BASE_CURRENCY] = currency }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE_ENABLED] = enabled }
    }

    suspend fun setDailyReminder(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DAILY_REMINDER_ENABLED] = enabled }
    }

    suspend fun setWeeklyReminder(enabled: Boolean) {
        context.dataStore.edit { it[Keys.WEEKLY_REMINDER_ENABLED] = enabled }
    }

    suspend fun setSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SYNC_ENABLED] = enabled }
    }

    suspend fun setLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { it[Keys.LAST_SYNC_TIMESTAMP] = timestamp.toDouble() }
    }
}
