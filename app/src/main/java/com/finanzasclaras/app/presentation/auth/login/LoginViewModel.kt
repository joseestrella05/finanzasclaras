package com.finanzasclaras.app.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.domain.repository.AuthRepository
import com.finanzasclaras.app.domain.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
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
            when (val result = authRepository.login(s.email, s.password)) {
                is AuthResult.Success -> {
                    userPreferences.setLoggedIn(result.userId, s.email)
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun useWithoutAccount() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSuccess = true)
        }
    }
}
