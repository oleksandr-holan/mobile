package com.example.lab1.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val notificationsEnabledFlow: Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)

    val soundsEnabledFlow: Flow<Boolean>
    suspend fun setSoundsEnabled(enabled: Boolean)

    val appThemeFlow: Flow<String>
    suspend fun setAppTheme(theme: String)

    val appLanguageFlow: Flow<String>
    suspend fun setAppLanguage(language: String)

    val loggedInUserUsernameFlow: Flow<String?>
    suspend fun setLoggedInUserUsername(username: String?)

    companion object {
        const val DEFAULT_THEME = "System"
        const val DEFAULT_LANGUAGE = "English"
    }
}