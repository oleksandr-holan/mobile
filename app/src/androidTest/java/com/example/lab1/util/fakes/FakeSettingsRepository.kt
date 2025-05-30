package com.example.lab1.util.fakes

import com.example.lab1.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FakeSettingsRepository @Inject constructor() : SettingsRepository {
    private val _loggedInUser = MutableStateFlow<String?>(null)
    private val _notificationsEnabled = MutableStateFlow(true)
    private val _soundsEnabled = MutableStateFlow(true)
    private val _appTheme = MutableStateFlow(SettingsRepository.DEFAULT_THEME)
    private val _appLanguage = MutableStateFlow(SettingsRepository.DEFAULT_LANGUAGE)


    override val notificationsEnabledFlow: Flow<Boolean> = _notificationsEnabled.asStateFlow()
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    override val soundsEnabledFlow: Flow<Boolean> = _soundsEnabled.asStateFlow()
    override suspend fun setSoundsEnabled(enabled: Boolean) {
        _soundsEnabled.value = enabled
    }

    override val appThemeFlow: Flow<String> = _appTheme.asStateFlow()
    override suspend fun setAppTheme(theme: String) {
        _appTheme.value = theme
    }

    override val appLanguageFlow: Flow<String> = _appLanguage.asStateFlow()
    override suspend fun setAppLanguage(language: String) {
        _appLanguage.value = language
    }

    override val loggedInUserUsernameFlow: Flow<String?> = _loggedInUser.asStateFlow()
    override suspend fun setLoggedInUserUsername(username: String?) {
        _loggedInUser.value = username
    }
}