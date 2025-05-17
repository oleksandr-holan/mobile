package com.example.lab1.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.util.DataResult
import com.example.lab1.data.repository.MockProfileRepository // For getCurrentUserProfile
import com.example.lab1.data.repository.ProfileRepository
import com.example.lab1.data.repository.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- State ---
data class ProfileScreenState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// --- Actions ---
sealed class ProfileScreenAction {
    data object LoadProfile : ProfileScreenAction()
    data object LogoutClicked : ProfileScreenAction() // Example action
    // Add more actions like EditProfile, ChangePassword
}

// --- Side Effects ---
sealed class ProfileScreenSideEffect {
    data object NavigateToLogin : ProfileScreenSideEffect() // After logout
    // data object NavigateToEditProfile : ProfileScreenSideEffect()
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository // Injected
    // In a real app, you might also inject an AuthRepository for logout
    // private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileScreenState())
    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    private val _sideEffect = kotlinx.coroutines.flow.MutableSharedFlow<ProfileScreenSideEffect>()
    val sideEffect: kotlinx.coroutines.flow.SharedFlow<ProfileScreenSideEffect> = _sideEffect.asSharedFlow()

    init {
        onAction(ProfileScreenAction.LoadProfile)
    }

    fun onAction(action: ProfileScreenAction) {
        when (action) {
            ProfileScreenAction.LoadProfile -> {
                fetchUserProfile()
            }
            ProfileScreenAction.LogoutClicked -> {
                // Simulate logout
                viewModelScope.launch {
                    // In a real app:
                    // authRepository.logout()
                    // _uiState.update { it.copy(userProfile = null, isLoading = false) }
                    _sideEffect.emit(ProfileScreenSideEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // In a real app, you'd get the current user's ID from an auth manager/state
            // For this example, MockProfileRepository has a helper or we assume a fixed ID
            val result = if (profileRepository is MockProfileRepository) {
                profileRepository.getCurrentUserProfile() // Use helper if available
            } else {
                // Fallback or get ID from somewhere else
                profileRepository.getUserProfile("testuser") // Defaulting for non-mock
            }

            when (result) {
                is DataResult.Success -> {
                    _uiState.update {
                        it.copy(
                            userProfile = result.data,
                            isLoading = false
                        )
                    }
                }
                is DataResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                }
                is DataResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}