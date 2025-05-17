package com.example.lab1.ui.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab1.ui.theme.Lab1Theme

@Composable
fun ProfileScreen() {
    // The main screen composable (like MainAppScreen) will handle Scaffold padding.
    // We might only need specific padding for content *within* this screen.
    Surface(modifier = Modifier.fillMaxSize()) { // Let Scaffold handle insets
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Text("Profile Screen Content")
            // TODO: Add profile details, settings, logout button etc. later
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    Lab1Theme {
        ProfileScreen()
    }
}