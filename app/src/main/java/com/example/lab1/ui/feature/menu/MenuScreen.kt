package com.example.lab1.ui.feature.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.example.lab1.ui.components.MenuItemCard // Reusing the existing MenuItemCard

@Composable
fun MenuScreen(
    onNavigateToNewOrderItemDetails: (menuItemId: String) -> Unit,
    menuViewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by menuViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(menuViewModel.sideEffect, lifecycleOwner) {
        menuViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collect { effect ->
            when (effect) {
                is MenuScreenSideEffect.NavigateToNewOrderItemDetails -> {
                    onNavigateToNewOrderItemDetails(effect.menuItemId)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Apply padding here, Scaffold will handle overall padding
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text("Loading menu...", modifier = Modifier.padding(start = 8.dp))
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (uiState.menuItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No menu items available.", modifier = Modifier.padding(16.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.menuItems, key = { item -> item.id }) { menuItem ->
                    Box(modifier = Modifier.clickable {
                        menuViewModel.onAction(MenuScreenAction.MenuItemClicked(menuItem.id))
                    }) {
                        MenuItemCard( // Assuming MenuItemCard takes these parameters
                            itemName = menuItem.name,
                            itemDescription = menuItem.description,
                            price = menuItem.price // Ensure MenuItemCard can handle the price format
                        )
                    }
                }
            }
        }
    }
}