package com.example.lab1.ui.feature.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.repository.AuthRepository
import com.example.lab1.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationScreenState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val dateOfBirth: String = "",
    val isPrivacyPolicyAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterEnabled: Boolean = false
)

sealed class RegistrationUiAction {
    data class UsernameChanged(val username: String) : RegistrationUiAction()
    data class PasswordChanged(val password: String) : RegistrationUiAction()
    data object TogglePasswordVisibility : RegistrationUiAction()
    data class DateOfBirthChanged(val dob: String) : RegistrationUiAction()
    data class PrivacyPolicyAcceptedChanged(val isAccepted: Boolean) : RegistrationUiAction()
    data object RegisterClicked : RegistrationUiAction()
    data object ErrorMessageDismissed : RegistrationUiAction()
}

sealed class RegistrationSideEffect {
    data object NavigateToMainApp : RegistrationSideEffect()
}

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationScreenState())
    val uiState: StateFlow<RegistrationScreenState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<RegistrationSideEffect>()
    val sideEffect: SharedFlow<RegistrationSideEffect> = _sideEffect.asSharedFlow()

    init {
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
                currentState.dateOfBirth.isNotBlank() &&
                currentState.isPrivacyPolicyAccepted
        _uiState.update { it.copy(isRegisterEnabled = isEnabled) }
    }

    private fun attemptRegistration() {
        val currentState = _uiState.value
        if (!currentState.isRegisterEnabled) {
            _uiState.update { it.copy(errorMessage = "fill_all_fields_and_accept_policy_error") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.register(
                username = currentState.username,
                passwordHash = currentState.password,
                dateOfBirth = currentState.dateOfBirth
            )) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
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