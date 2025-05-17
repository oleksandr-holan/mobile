package com.example.lab1.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Orders : BottomNavItem(
        route = AppDestinations.ORDER_LIST_ROUTE, // Use the main screen route
        title = "Orders", // Title for the bottom bar item
        icon = Icons.AutoMirrored.Filled.ListAlt
    )
    // Changed History to Profile
    data object Profile : BottomNavItem(
        route = AppDestinations.PROFILE_ROUTE, // Use profile route
        title = "Profile",
        icon = Icons.Default.Person
    )
}

// Update the list
val bottomNavItems = listOf(
    BottomNavItem.Orders,
    BottomNavItem.Profile
)