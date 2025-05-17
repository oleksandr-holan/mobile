package com.example.lab1.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack 
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab1.ui.theme.Lab1Theme

@OptIn(ExperimentalMaterial3Api::class) 
@Composable
fun AppTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back" 
                    )
                }
            }
        },
    )
}

@Preview
@Composable
fun AppTopAppBarPreviewBack() {
    Lab1Theme {
        AppTopAppBar(title = "Details", canNavigateBack = true, onNavigateBack = {})
    }
}

@Preview
@Composable
fun AppTopAppBarPreviewNoBack() {
    Lab1Theme {
        AppTopAppBar(title = "Orders", canNavigateBack = false, onNavigateBack = {})
    }
}