package com.finanzasclaras.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.setOnboardingCompleted()
        }
    }
}
