package com.example.lab1.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.util.DataResult
import com.example.lab1.data.repository.MockProfileRepository
import com.example.lab1.data.repository.ProfileRepository
import com.example.lab1.data.repository.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class ProfileScreenAction {
    data object LoadProfile : ProfileScreenAction()
    data object LogoutClicked : ProfileScreenAction()
}

sealed class ProfileScreenSideEffect {
    data object NavigateToLogin : ProfileScreenSideEffect()
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileScreenState())
    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    private val _sideEffect = kotlinx.coroutines.flow.MutableSharedFlow<ProfileScreenSideEffect>()
    val sideEffect: kotlinx.coroutines.flow.SharedFlow<ProfileScreenSideEffect> =
        _sideEffect.asSharedFlow()

    init {
        onAction(ProfileScreenAction.LoadProfile)
    }

    fun onAction(action: ProfileScreenAction) {
        when (action) {
            ProfileScreenAction.LoadProfile -> {
                fetchUserProfile()
            }

            ProfileScreenAction.LogoutClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(ProfileScreenSideEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (profileRepository is MockProfileRepository) {
                profileRepository.getCurrentUserProfile()
            } else {
                profileRepository.getUserProfile("testuser")
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