package com.example.lab1.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.appSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")