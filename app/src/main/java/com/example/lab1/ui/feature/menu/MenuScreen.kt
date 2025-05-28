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
import com.example.lab1.ui.components.MenuItemCard 
import androidx.compose.ui.res.stringResource
import com.example.lab1.R
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.lab1.ui.feature.dailyspecials.DailySpecialsActivity

@Composable
fun MenuScreen(
    onNavigateToNewOrderItemDetails: (menuItemId: String) -> Unit,
    menuViewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by menuViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

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
            .padding(16.dp) 
    ) {
        Button(
            onClick = {
                context.startActivity(Intent(context, DailySpecialsActivity::class.java))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(stringResource(R.string.view_daily_specials_button))
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text(stringResource(R.string.loading_menu_text), modifier = Modifier.padding(start = 8.dp))
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = when (uiState.errorMessage) {
                        "error_fetching_menu_error" -> stringResource(R.string.error_fetching_menu_error)
                        else -> stringResource(R.string.generic_error_text, uiState.errorMessage!!) // Fallback
                    },
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (uiState.menuItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_menu_items_text), modifier = Modifier.padding(16.dp))
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
                        MenuItemCard( 
                            itemNameKey = menuItem.nameKey,
                            itemDescriptionKey = menuItem.descriptionKey,
                            price = menuItem.price 
                        )
                    }
                }
            }
        }
    }
}