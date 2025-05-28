package com.example.lab1.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.lab1.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
) {
    data object Orders : BottomNavItem(
        route = AppDestinations.ORDER_LIST_ROUTE,
        titleResId = R.string.orders_title,
        icon = Icons.AutoMirrored.Filled.ListAlt
    )

    data object Profile : BottomNavItem(
        route = AppDestinations.PROFILE_ROUTE,
        titleResId = R.string.profile_title,
        icon = Icons.Default.Person
    )

    data object Settings : BottomNavItem(
        route = AppDestinations.SETTINGS_ROUTE,
        titleResId = R.string.settings_title,
        icon = Icons.Default.Settings
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Orders,
    BottomNavItem.Profile,
    BottomNavItem.Settings,
)