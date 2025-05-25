package com.example.lab1.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.lab1.ui.feature.order.OrderScreen
import com.example.lab1.ui.feature.profile.ProfileScreen
import com.example.lab1.ui.feature.settings.SettingsScreen
import com.example.lab1.ui.feature.item.AddItemDetailsScreen

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
                // OrderScreen will now manage its own ViewModel instance
                OrderScreen(
                    innerNavController = innerNavController
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

            composable(
                route = AppDestinations.MENU_SCREEN_WITH_ORDER_ROUTE,
                arguments = listOf(
                    navArgument(AppDestinations.ARG_ACTIVE_ORDER_ID) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val activeOrderId = backStackEntry.arguments?.getLong(AppDestinations.ARG_ACTIVE_ORDER_ID)
                if (activeOrderId != null && activeOrderId != 0L) {
                    MenuScreen(
                        onNavigateToNewOrderItemDetails = { menuItemId ->
                            innerNavController.navigate(
                                AppDestinations.NEW_ORDER_ITEM_DETAILS_ROUTE
                                    .replace("{${AppDestinations.ARG_MENU_ITEM_ID}}", menuItemId)
                                    .replace("{${AppDestinations.ARG_ACTIVE_ORDER_ID}}", activeOrderId.toString())
                            )
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: Active order ID is missing for Menu Screen.")
                    }
                }
            }

            composable(
                route = AppDestinations.NEW_ORDER_ITEM_DETAILS_ROUTE,
                arguments = listOf(
                    navArgument(AppDestinations.ARG_MENU_ITEM_ID) { type = NavType.StringType },
                    navArgument(AppDestinations.ARG_ACTIVE_ORDER_ID) { type = NavType.LongType }
                )
            ) {
                AddItemDetailsScreen(
                    onNavigateBack = { innerNavController.popBackStack() }
                )
            }

            composable(
                route = AppDestinations.EDIT_ORDER_ITEM_DETAILS_ROUTE,
                arguments = listOf(
                    navArgument(AppDestinations.ARG_ITEM_ID) { type = NavType.LongType }
                )
            ) {
                AddItemDetailsScreen(
                    onNavigateBack = { innerNavController.popBackStack() }
                )
            }

            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}