package com.finanzasclaras.app.presentation.splash

import androidx.lifecycle.ViewModel
import com.finanzasclaras.app.core.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

data class SplashUiState(
    val isOnboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state

    init {
        runBlocking {
            val prefs = userPreferences.preferences.first()
            _state.value = SplashUiState(
                isOnboardingCompleted = prefs.isOnboardingCompleted,
                isLoggedIn = prefs.isLoggedIn
            )
        }
    }
}
