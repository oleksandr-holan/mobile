package com.example.lab1.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt // Icon for Orders
import androidx.compose.material.icons.filled.History // Icon for History
import androidx.compose.ui.graphics.vector.ImageVector

// Represents items in the Bottom Navigation Bar
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Orders : BottomNavItem(
        route = AppDestinations.ORDER_LIST_ROUTE, // Reuse route from AppDestinations
        title = "Orders",
        icon = Icons.Default.ListAlt
    )
    // Add more items here, e.g., History
    object History : BottomNavItem(
        route = "history", // Define a new route for history
        title = "History",
        icon = Icons.Default.History
    )
    // Add Profile, Settings etc. as needed
}

// Helper list for easy iteration
val bottomNavItems = listOf(
    BottomNavItem.Orders,
    BottomNavItem.History
    // Add other items here
)