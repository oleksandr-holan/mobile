package com.example.lab1.ui.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val soundsEnabled: Boolean = true,
    val appTheme: String = SettingsRepository.DEFAULT_THEME,
    val appLanguage: String = SettingsRepository.DEFAULT_LANGUAGE
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val notificationsEnabled: StateFlow<Boolean> = settingsRepository.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val soundsEnabled: StateFlow<Boolean> = settingsRepository.soundsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val appTheme: StateFlow<String> = settingsRepository.appThemeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsRepository.DEFAULT_THEME)

    val appLanguage: StateFlow<String> = settingsRepository.appLanguageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsRepository.DEFAULT_LANGUAGE)

    fun onNotificationsEnabledChanged(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }

    fun onSoundsEnabledChanged(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSoundsEnabled(enabled)
        }
    }

    fun onAppThemeChanged(theme: String) {
        viewModelScope.launch {
            settingsRepository.setAppTheme(theme)
        }
    }

    fun onAppLanguageChanged(language: String) {
        viewModelScope.launch {
            settingsRepository.setAppLanguage(language)
        }
    }
}