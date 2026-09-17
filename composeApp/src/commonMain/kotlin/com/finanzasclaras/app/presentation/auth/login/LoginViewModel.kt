package com.finanzasclaras.app.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.domain.repository.AuthRepository
import com.finanzasclaras.app.domain.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    private val syncManager: FirebaseSyncManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _state.value = _state.value.copy(password = password, error = null)
    }

    fun login() {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            try {
                when (val result = authRepository.login(s.email, s.password)) {
                    is AuthResult.Success -> {
                        userPreferences.setLoggedIn(result.userId, s.email)
                        try {
                            syncManager.syncAll()
                        } catch (_: Throwable) {}
                        _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                    }
                    is AuthResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isLoading = false, error = t.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun useWithoutAccount() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSuccess = true)
        }
    }
}
