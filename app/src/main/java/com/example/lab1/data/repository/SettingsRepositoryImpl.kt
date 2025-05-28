package com.example.lab1.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.lab1.data.preferences.AppSettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val notificationsEnabledFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[AppSettingsKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    override val soundsEnabledFlow: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[AppSettingsKeys.SOUNDS_ENABLED] ?: true
        }

    override suspend fun setSoundsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.SOUNDS_ENABLED] = enabled
        }
    }

    override val appThemeFlow: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[AppSettingsKeys.APP_THEME] ?: SettingsRepository.DEFAULT_THEME
        }

    override suspend fun setAppTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.APP_THEME] = theme
        }
    }

    override val appLanguageFlow: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[AppSettingsKeys.APP_LANGUAGE] ?: SettingsRepository.DEFAULT_LANGUAGE
        }

    override suspend fun setAppLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.APP_LANGUAGE] = language
        }
    }

    override val loggedInUserUsernameFlow: Flow<String?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[AppSettingsKeys.LOGGED_IN_USER_USERNAME]
        }

    override suspend fun setLoggedInUserUsername(username: String?) {
        dataStore.edit { preferences ->
            if (username != null) {
                preferences[AppSettingsKeys.LOGGED_IN_USER_USERNAME] = username
            } else {
                preferences.remove(AppSettingsKeys.LOGGED_IN_USER_USERNAME)
            }
        }
    }
}