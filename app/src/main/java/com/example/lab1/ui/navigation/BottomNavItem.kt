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
        route = AppDestinations.ORDER_LIST_ROUTE,
        title = "Orders",
        icon = Icons.AutoMirrored.Filled.ListAlt
    )

    data object Profile : BottomNavItem(
        route = AppDestinations.PROFILE_ROUTE,
        title = "Profile",
        icon = Icons.Default.Person
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Orders,
    BottomNavItem.Profile
)