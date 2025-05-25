package com.example.lab1.ui.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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
            title = "Enable Notifications",
            checked = notificationsEnabled,
            onCheckedChange = { settingsViewModel.onNotificationsEnabledChanged(it) }
        )
        HorizontalDivider()
        SettingItemSwitch(
            title = "Enable Sounds",
            checked = soundsEnabled,
            onCheckedChange = { settingsViewModel.onSoundsEnabledChanged(it) }
        )
        HorizontalDivider()
        ThemeSettingItem(
            currentTheme = currentTheme,
            onThemeSelected = { settingsViewModel.onAppThemeChanged(it) }
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
    val themes =
        listOf("Light", "Dark", "System")

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("App Theme", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = currentTheme,
                onValueChange = { },
                readOnly = true,
                label = { Text("Selected Theme") },
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
                themes.forEach { theme ->
                    DropdownMenuItem(
                        text = { Text(theme) },
                        onClick = {
                            onThemeSelected(theme)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}