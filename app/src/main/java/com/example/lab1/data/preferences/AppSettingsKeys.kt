package com.example.lab1.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppSettingsKeys {
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val SOUNDS_ENABLED = booleanPreferencesKey("sounds_enabled")
    val APP_THEME = stringPreferencesKey("app_theme")
    val APP_LANGUAGE = stringPreferencesKey("app_language")
    val LOGGED_IN_USER_USERNAME = stringPreferencesKey("logged_in_user_username")
}