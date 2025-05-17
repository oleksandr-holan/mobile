package com.example.lab1.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab1.data.repository.ProfileRepository
// import com.example.lab1.data.repository.AuthRepository // If ViewModel needs it

class ProfileViewModelFactory(
    private val profileRepository: ProfileRepository
    // private val authRepository: AuthRepository // If injecting for logout
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            // return ProfileViewModel(profileRepository, authRepository) as T // If using authRepo
            return ProfileViewModel(profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}