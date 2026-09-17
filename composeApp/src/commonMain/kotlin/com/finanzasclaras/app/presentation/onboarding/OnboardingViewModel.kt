package com.finanzasclaras.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import kotlinx.coroutines.launch

class OnboardingViewModel constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.setOnboardingCompleted()
        }
    }
}
