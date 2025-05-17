package com.example.lab1.ui.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import remember, derivedStateOf, etc.
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
import com.example.lab1.ui.navigation.bottomNavItems // Import list
// Import components
import com.example.lab1.ui.components.AppBottomNavigationBar
import com.example.lab1.ui.components.AppTopAppBar
import com.example.lab1.ui.feature.item.AddItemDetailsScreen
import com.example.lab1.ui.feature.order.OrderScreen
import com.example.lab1.ui.feature.profile.ProfileScreen

@Composable
fun MainAppScreen(outerNavController: NavHostController) {
    val innerNavController: NavHostController = rememberNavController()

    // Get current back stack entry to determine route, title, and back button visibility
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine the title based on the current route
    val currentScreenTitle = remember(currentRoute) {
        when (currentRoute) {
            BottomNavItem.Orders.route -> BottomNavItem.Orders.title
            BottomNavItem.Profile.route -> BottomNavItem.Profile.title
            AppDestinations.ADD_ITEM_DETAILS_ROUTE -> "Item Details" // Or get dynamically
            else -> "" // Default or loading title
        }
    }

    // Determine if the back button should be shown
    // Show back if the current route is NOT one of the main bottom nav destinations
    val canNavigateBack = remember(currentRoute) {
        bottomNavItems.none { it.route == currentRoute } && currentRoute != null
        // Alternative: check innerNavController.previousBackStackEntry != null
    }


    Scaffold(
        topBar = {
            AppTopAppBar(
                title = currentScreenTitle,
                canNavigateBack = canNavigateBack,
                onNavigateBack = { innerNavController.popBackStack() } // Simple back action
            )
        },
        bottomBar = { AppBottomNavigationBar(navController = innerNavController) }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = BottomNavItem.Orders.route, // Start on Orders screen
            modifier = Modifier.padding(innerPadding) // Apply padding from Scaffold
        ) {
            composable(BottomNavItem.Orders.route) {
                OrderScreen(
                    // Pass lambda to navigate to details using the inner controller
                    onNavigateToAddItem = { itemId -> // Lambda now only takes itemId
                        innerNavController.navigate(
                            // Navigate with only itemId in the path
                            "${AppDestinations.ADD_ITEM_DETAILS_ROUTE}/$itemId"
                        )
                    }
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = {
                        outerNavController.navigate(AppDestinations.LOGIN_ROUTE) {
                            popUpTo(AppDestinations.MAIN_APP_ROUTE) {
                                inclusive = true
                            } // Pop MainAppScreen itself
                            launchSingleTop = true
                        }
                    }
                    // viewModel(...)
                )
            }
            composable(
                route = AppDestinations.ADD_ITEM_DETAILS_WITH_ID_ROUTE, // Uses the route with itemId
                arguments = listOf(
                    navArgument(AppDestinations.ARG_ITEM_ID) { type = NavType.StringType }
                )
            ) {
                // ViewModel inside AddItemDetailsScreen will pick up itemId from SavedStateHandle
                AddItemDetailsScreen(
                    onNavigateBack = { innerNavController.popBackStack() }
                )
            }
        }
    }
}