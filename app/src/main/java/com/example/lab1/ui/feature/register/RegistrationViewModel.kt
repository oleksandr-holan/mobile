package com.example.lab1.ui.feature.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.repository.AuthRepository
import com.example.lab1.data.repository.AuthResult
// If you create a factory, you might not need to import MockAuthRepository here directly
// import com.example.lab1.data.repository.MockAuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- State ---
data class RegistrationScreenState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val dateOfBirth: String = "", // For simplicity, kept as String
    val isPrivacyPolicyAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterEnabled: Boolean = false
)

// --- Actions (Events from UI to ViewModel) ---
sealed class RegistrationUiAction {
    data class UsernameChanged(val username: String) : RegistrationUiAction()
    data class PasswordChanged(val password: String) : RegistrationUiAction()
    data object TogglePasswordVisibility : RegistrationUiAction()
    data class DateOfBirthChanged(val dob: String) : RegistrationUiAction()
    data class PrivacyPolicyAcceptedChanged(val isAccepted: Boolean) : RegistrationUiAction()
    data object RegisterClicked : RegistrationUiAction()
    data object ErrorMessageDismissed : RegistrationUiAction()
}

// --- Side Effects (One-time events from ViewModel to UI) ---
sealed class RegistrationSideEffect {
    data object NavigateToMainApp : RegistrationSideEffect()
    // You could add: data object NavigateBackToLogin : RegistrationSideEffect() if needed from ViewModel
}

class RegistrationViewModel(
    private val authRepository: AuthRepository // Expect AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationScreenState())
    val uiState: StateFlow<RegistrationScreenState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<RegistrationSideEffect>()
    val sideEffect: SharedFlow<RegistrationSideEffect> = _sideEffect.asSharedFlow()

    init {
        // Initial check for button enabled state based on default empty values
        updateRegisterButtonState(_uiState.value)
    }

    fun onAction(action: RegistrationUiAction) {
        when (action) {
            is RegistrationUiAction.UsernameChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        username = action.username,
                        errorMessage = null
                    ).also { updateRegisterButtonState(it) }
                }
            }
            is RegistrationUiAction.PasswordChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        password = action.password,
                        errorMessage = null
                    ).also { updateRegisterButtonState(it) }
                }
            }
            RegistrationUiAction.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is RegistrationUiAction.DateOfBirthChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        dateOfBirth = action.dob,
                        errorMessage = null
                    ).also { updateRegisterButtonState(it) }
                }
            }
            is RegistrationUiAction.PrivacyPolicyAcceptedChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        isPrivacyPolicyAccepted = action.isAccepted,
                        errorMessage = null
                    ).also { updateRegisterButtonState(it) }
                }
            }
            RegistrationUiAction.RegisterClicked -> {
                attemptRegistration()
            }
            RegistrationUiAction.ErrorMessageDismissed -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun updateRegisterButtonState(currentState: RegistrationScreenState) {
        val isEnabled = currentState.username.isNotBlank() &&
                currentState.password.isNotBlank() &&
                currentState.dateOfBirth.isNotBlank() && // Basic check, consider date validation
                currentState.isPrivacyPolicyAccepted
        _uiState.update { it.copy(isRegisterEnabled = isEnabled) }
    }

    private fun attemptRegistration() {
        val currentState = _uiState.value
        if (!currentState.isRegisterEnabled) {
            // This case should ideally not be hit if button is properly disabled, but as a safeguard:
            _uiState.update { it.copy(errorMessage = "Please fill all required fields and accept the privacy policy.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // In a real app, you'd hash the password before sending it to the repository,
            // or the repository/backend would handle hashing. For this mock, we pass it as is.
            when (val result = authRepository.register(
                username = currentState.username,
                passwordHash = currentState.password, // Ideally hash this
                dateOfBirth = currentState.dateOfBirth
            )) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    // Optionally, you could auto-login the user here by calling authRepository.login
                    // and then navigate, or just navigate directly to main app assuming successful registration means logged in.
                    _sideEffect.emit(RegistrationSideEffect.NavigateToMainApp)
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