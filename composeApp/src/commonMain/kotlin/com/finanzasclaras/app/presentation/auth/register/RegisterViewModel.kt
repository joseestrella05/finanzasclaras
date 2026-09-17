package com.finanzasclaras.app.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.domain.repository.AuthRepository
import com.finanzasclaras.app.domain.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    fun onNameChanged(name: String) { _state.value = _state.value.copy(name = name, error = null) }
    fun onEmailChanged(email: String) { _state.value = _state.value.copy(email = email, error = null) }
    fun onPasswordChanged(password: String) { _state.value = _state.value.copy(password = password, error = null) }
    fun onConfirmPasswordChanged(confirmPassword: String) { _state.value = _state.value.copy(confirmPassword = confirmPassword, error = null) }

    fun register() {
        val s = _state.value
        if (s.name.isBlank() || s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "Completa todos los campos")
            return
        }
        if (s.password != s.confirmPassword) {
            _state.value = s.copy(error = "Las contraseñas no coinciden")
            return
        }
        if (s.password.length < 6) {
            _state.value = s.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            try {
                when (val result = authRepository.register(s.email, s.password, s.name)) {
                    is AuthResult.Success -> {
                        userPreferences.setLoggedIn(result.userId, s.name)
                        _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                    }
                    is AuthResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isLoading = false, error = t.message ?: "Error al registrar")
            }
        }
    }

    fun useWithoutAccount() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSuccess = true)
        }
    }
}
