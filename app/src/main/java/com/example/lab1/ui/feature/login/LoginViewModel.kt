package com.example.lab1.ui.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.repository.AuthRepository
import com.example.lab1.data.repository.AuthResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginEnabled: Boolean = false
)

sealed class LoginUiAction {
    data class UsernameChanged(val username: String) : LoginUiAction()
    data class PasswordChanged(val password: String) : LoginUiAction()
    data object TogglePasswordVisibility : LoginUiAction()
    data object LoginClicked : LoginUiAction()
    data object ErrorMessageDismissed : LoginUiAction()
}

sealed class LoginSideEffect {
    data object NavigateToMainApp : LoginSideEffect()
}

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState: StateFlow<LoginScreenState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<LoginSideEffect>()
    val sideEffect: SharedFlow<LoginSideEffect> = _sideEffect.asSharedFlow()

    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.UsernameChanged -> {
                _uiState.update { currentState ->
                    val newUsername = action.username
                    currentState.copy(
                        username = newUsername,
                        errorMessage = null,
                        isLoginEnabled = newUsername.isNotBlank() && currentState.password.isNotBlank()
                    )
                }
            }

            is LoginUiAction.PasswordChanged -> {
                _uiState.update { currentState ->
                    val newPassword = action.password
                    currentState.copy(
                        password = newPassword,
                        errorMessage = null,
                        isLoginEnabled = currentState.username.isNotBlank() && newPassword.isNotBlank()
                    )
                }
            }

            LoginUiAction.TogglePasswordVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
                }
            }

            LoginUiAction.LoginClicked -> {
                attemptLogin()
            }

            LoginUiAction.ErrorMessageDismissed -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun attemptLogin() {
        val currentState = _uiState.value

        if (!currentState.isLoginEnabled) {
            _uiState.update { it.copy(errorMessage = "username_password_required_error") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.login(currentState.username, currentState.password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _sideEffect.emit(LoginSideEffect.NavigateToMainApp)
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}