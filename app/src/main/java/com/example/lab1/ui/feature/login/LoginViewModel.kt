package com.example.lab1.ui.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.repository.AuthRepository
import com.example.lab1.data.repository.AuthResult
import kotlinx.coroutines.delay // For simulation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- State ---
// Represents all the data needed to render the Login UI
data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false, // To show a loading indicator during login attempt
    val errorMessage: String? = null, // To display any login errors
    val isLoginEnabled: Boolean = false // Derived from username and password presence
)

// --- Actions (Events from UI to ViewModel) ---
// Represents user interactions or events originating from the UI
sealed class LoginUiAction {
    data class UsernameChanged(val username: String) : LoginUiAction()
    data class PasswordChanged(val password: String) : LoginUiAction()
    data object TogglePasswordVisibility : LoginUiAction()
    data object LoginClicked : LoginUiAction()
    data object ErrorMessageDismissed : LoginUiAction() // If you want to allow dismissing errors
}

// --- Side Effects (One-time events from ViewModel to UI) ---
// Represents events that the UI should handle once, like navigation or showing a Toast/Snackbar
sealed class LoginSideEffect {
    data object NavigateToMainApp : LoginSideEffect()
    // You could add others like: data class ShowSnackbar(val message: String) : LoginSideEffect()
}

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState: StateFlow<LoginScreenState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<LoginSideEffect>() // For one-time events
    val sideEffect: SharedFlow<LoginSideEffect> = _sideEffect.asSharedFlow()

    /**
     * Handles actions dispatched from the UI.
     */
    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.UsernameChanged -> {
                _uiState.update { currentState ->
                    val newUsername = action.username
                    currentState.copy(
                        username = newUsername,
                        errorMessage = null, // Clear error when user types
                        isLoginEnabled = newUsername.isNotBlank() && currentState.password.isNotBlank()
                    )
                }
            }

            is LoginUiAction.PasswordChanged -> {
                _uiState.update { currentState ->
                    val newPassword = action.password
                    currentState.copy(
                        password = newPassword,
                        errorMessage = null, // Clear error when user types
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
        // Basic check, though button should be disabled if not valid
        if (!currentState.isLoginEnabled) {
            _uiState.update { it.copy(errorMessage = "Username and password are required.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Call the repository
            when (val result = authRepository.login(currentState.username, currentState.password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _sideEffect.emit(LoginSideEffect.NavigateToMainApp) // Emit navigation event
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