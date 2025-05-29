package com.example.lab1.ui.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lab1.R

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    val soundsEnabled by settingsViewModel.soundsEnabled.collectAsState()
    val currentTheme by settingsViewModel.appTheme.collectAsState()
    val currentLanguage by settingsViewModel.appLanguage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingItemSwitch(
            title = stringResource(R.string.notifications_enabled_label),
            checked = notificationsEnabled,
            onCheckedChange = { settingsViewModel.onNotificationsEnabledChanged(it) }
        )
        SettingItemSwitch(
            title = stringResource(R.string.sounds_enabled_label),
            checked = soundsEnabled,
            onCheckedChange = { settingsViewModel.onSoundsEnabledChanged(it) }
        )
        ThemeSettingItem(
            currentTheme = currentTheme,
            onThemeSelected = { settingsViewModel.onAppThemeChanged(it) }
        )
        LanguageSettingItem(
            currentLanguage = currentLanguage,
            onLanguageSelected = { newLanguage ->
                if (newLanguage != currentLanguage) {
                    settingsViewModel.onAppLanguageChanged(newLanguage)
                }
            }
        )
    }
}

@Composable
fun SettingItemSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ThemeSettingItem(
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val themes = listOf(
        stringResource(R.string.light_theme),
        stringResource(R.string.dark_theme),
        stringResource(R.string.system_theme)
    )
    val themeKeys = listOf("Light", "Dark", "System")


    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(stringResource(R.string.app_theme_label), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = when (currentTheme) {
                    "Light" -> stringResource(R.string.light_theme)
                    "Dark" -> stringResource(R.string.dark_theme)
                    else -> stringResource(R.string.system_theme)
                },
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.selected_theme_label)) },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "dropdown") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                themes.forEachIndexed { index, themeName ->
                    DropdownMenuItem(
                        text = { Text(themeName) },
                        onClick = {
                            onThemeSelected(themeKeys[index])
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSettingItem(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf(
        "en" to stringResource(R.string.english),
        "uk" to stringResource(R.string.ukrainian)
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            stringResource(R.string.app_language_label),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = languages.find { it.first == currentLanguage }?.second ?: currentLanguage,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.selected_language_label)) },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "dropdown") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                languages.forEach { (langCode, langName) ->
                    DropdownMenuItem(
                        text = { Text(langName) },
                        onClick = {
                            onLanguageSelected(langCode)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}