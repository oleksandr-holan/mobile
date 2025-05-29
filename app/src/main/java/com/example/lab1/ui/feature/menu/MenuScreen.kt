package com.example.lab1.ui.feature.menu

import android.annotation.SuppressLint
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
import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// Helper function to get user-friendly error message
@SuppressLint("DiscouragedApi")
@Composable
private fun getDisplayErrorMessage(context: Context, errorMessage: String?): String {
    if (errorMessage == null) return stringResource(R.string.server_error_fallback) // Default to server error if message is null

    val jsonErrorPrefix = "api_call_failed_error: "
    if (errorMessage.startsWith(jsonErrorPrefix)) {
        val jsonString = errorMessage.substring(jsonErrorPrefix.length)
        try {
            val jsonElement = Json.parseToJsonElement(jsonString)
            val errorKey = jsonElement.jsonObject["errorKey"]?.jsonPrimitive?.content
            if (errorKey != null) {
                val resourceId = context.resources.getIdentifier(errorKey, "string", context.packageName)
                if (resourceId != 0) {
                    return context.getString(resourceId) // Specific error message found
                }
            }
        } catch (e: Exception) {
            Log.e("MenuScreen", "Failed to parse errorKey from errorMessage: $errorMessage", e)
            // Fall through to the server_error_fallback if parsing fails
        }
    }
    // If not a parsable api_call_failed_error, or if parsing/lookup failed, use the server_error_fallback
    return stringResource(R.string.server_error_fallback)
}

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
                    text = getDisplayErrorMessage(context, uiState.errorMessage),
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