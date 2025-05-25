package com.example.lab1.ui.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab1.ui.navigation.AppDestinations
import com.example.lab1.ui.navigation.BottomNavItem
import com.example.lab1.ui.navigation.bottomNavItems

import com.example.lab1.ui.components.AppBottomNavigationBar
import com.example.lab1.ui.components.AppTopAppBar
import com.example.lab1.ui.feature.menu.MenuScreen
//import com.example.lab1.ui.feature.item.AddItemDetailsScreen // Keep for now
import com.example.lab1.ui.feature.order.OrderScreen
import com.example.lab1.ui.feature.profile.ProfileScreen
import com.example.lab1.ui.feature.settings.SettingsScreen

@Composable
fun MainAppScreen(outerNavController: NavHostController) {
    val innerNavController: NavHostController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentScreenTitle = remember(currentRoute) {
        when {
            currentRoute == BottomNavItem.Orders.route -> BottomNavItem.Orders.title
            currentRoute == BottomNavItem.Profile.route -> BottomNavItem.Profile.title
            currentRoute == AppDestinations.MENU_SCREEN_ROUTE -> "Select Menu Items"
            currentRoute?.startsWith(AppDestinations.ADD_ITEM_DETAILS_ROUTE) == true -> "Item Details" // Catches both new/edit
            currentRoute == BottomNavItem.Settings.route -> BottomNavItem.Settings.title
            else -> "Order App"
        }
    }
    val canNavigateBack = remember(currentRoute) {
        // True if not a bottom nav item AND currentRoute is not null
        // AND currentRoute is not the start destination of the inner graph (Orders)
        bottomNavItems.none { it.route == currentRoute } &&
                currentRoute != null &&
                currentRoute != BottomNavItem.Orders.route
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = currentScreenTitle,
                canNavigateBack = canNavigateBack, // Enable back for MenuScreen and AddItemDetailsScreen
                onNavigateBack = { innerNavController.popBackStack() }
            )
        },
        bottomBar = {
            // Show bottom nav only for top-level bottom nav destinations
            if (bottomNavItems.any { it.route == currentRoute}) {
                AppBottomNavigationBar(navController = innerNavController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = BottomNavItem.Orders.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Orders.route) {
                OrderScreen(
                    onNavigateToMenu = {
                        innerNavController.navigate(AppDestinations.MENU_SCREEN_ROUTE)
                    },
                    onNavigateToEditOrderItem = { orderItemId ->
                        innerNavController.navigate(
                            AppDestinations.EDIT_ORDER_ITEM_DETAILS_ROUTE.replace(
                                "{${AppDestinations.ARG_ITEM_ID}}",
                                orderItemId.toString()
                            )
                        )
                    }
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = {
                        outerNavController.navigate(AppDestinations.LOGIN_ROUTE) {
                            popUpTo(AppDestinations.MAIN_APP_ROUTE) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Placeholder for Menu Screen
            composable(AppDestinations.MENU_SCREEN_ROUTE) {
                MenuScreen(
                    onNavigateToNewOrderItemDetails = { menuItemId ->
                        innerNavController.navigate(
                            AppDestinations.NEW_ORDER_ITEM_DETAILS_ROUTE.replace(
                                "{${AppDestinations.ARG_MENU_ITEM_ID}}",
                                menuItemId
                            )
                        )
                    }
                )
            }

            // Route for ADDING a new item from menu - ARG_MENU_ITEM_ID
            composable(
                route = AppDestinations.NEW_ORDER_ITEM_DETAILS_ROUTE,
                arguments = listOf(
                    navArgument(AppDestinations.ARG_MENU_ITEM_ID) { type = NavType.StringType }
                )
            ) {
                // We'll fully implement AddItemDetailsScreen with its ViewModel later
                // For now, it can use its existing structure or be a placeholder
//                AddItemDetailsScreen( // This will need its ViewModel factory for now
//                    onNavigateBack = { innerNavController.popBackStack() }
//                )
            }

            // Route for EDITING an existing order item - ARG_ITEM_ID (Long)
            composable(
                route = AppDestinations.EDIT_ORDER_ITEM_DETAILS_ROUTE,
                arguments = listOf(
                    navArgument(AppDestinations.ARG_ITEM_ID) { type = NavType.LongType } // Changed to LongType
                )
            ) {
                // Same AddItemDetailsScreen, but will load an OrderItemEntity
//                AddItemDetailsScreen( // This will need its ViewModel factory for now
//                    onNavigateBack = { innerNavController.popBackStack() }
//                )
            }

            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}