package com.example.lab1.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab1.ui.theme.Lab1Theme
import androidx.compose.ui.res.stringResource
import com.example.lab1.R

@OptIn(ExperimentalMaterial3Api::class) 
@Composable
fun AppTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_desc) 
                    )
                }
            }
        },
        actions = actions
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

@Preview
@Composable
fun AppTopAppBarPreviewWithActions() {
    Lab1Theme {
        AppTopAppBar(title = "Orders", canNavigateBack = false, onNavigateBack = {}, actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Favorite")
            }
        })
    }
}