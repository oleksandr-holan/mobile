package com.example.lab1.ui.feature.home

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag // Ensure this is imported
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
import androidx.compose.ui.res.stringResource
import com.example.lab1.R

import com.example.lab1.ui.components.AppBottomNavigationBar
import com.example.lab1.ui.components.AppTopAppBar
import com.example.lab1.ui.feature.menu.MenuScreen
import com.example.lab1.ui.feature.order.OrderScreen
import com.example.lab1.ui.feature.orderhistory.OrderHistoryActivity
import com.example.lab1.ui.feature.profile.ProfileScreen
import com.example.lab1.ui.feature.settings.SettingsScreen
import com.example.lab1.ui.feature.item.AddItemDetailsScreen

// Define a constant for the test tag
const val MAIN_APP_SCREEN_TITLE_TAG = "app_top_bar_title_main_app" // Used for testing the top bar title
const val MENU_SCREEN_MISSING_ORDER_ID_ERROR_TAG = "menu_screen_missing_order_id_error" // New Tag

@Composable
fun MainAppScreen(outerNavController: NavHostController) {
    val innerNavController: NavHostController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    val currentScreenTitle = currentRoute?.let {
        when {
            it == BottomNavItem.Orders.route -> stringResource(BottomNavItem.Orders.titleResId)
            it == BottomNavItem.Profile.route -> stringResource(BottomNavItem.Profile.titleResId)
            it == AppDestinations.MENU_SCREEN_ROUTE -> stringResource(R.string.select_menu_items_title)
            // Check for MENU_SCREEN_WITH_ORDER_ROUTE specifically for the title when on MenuScreen with an order ID
            it.startsWith(AppDestinations.MENU_SCREEN_ROUTE) -> stringResource(R.string.select_menu_items_title)
            it.startsWith(AppDestinations.ADD_ITEM_DETAILS_ROUTE) -> stringResource(R.string.item_details_title)
            it == BottomNavItem.Settings.route -> stringResource(BottomNavItem.Settings.titleResId)
            else -> stringResource(R.string.order_app_title)
        }
    } ?: stringResource(R.string.order_app_title)

    val canNavigateBack = remember(currentRoute) {
        bottomNavItems.none { it.route == currentRoute } &&
                currentRoute != null &&
                currentRoute != BottomNavItem.Orders.route
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = currentScreenTitle,
                canNavigateBack = canNavigateBack,
                onNavigateBack = { innerNavController.popBackStack() },
                actions = {
                    if (currentRoute == BottomNavItem.Orders.route) {
                        IconButton(onClick = {
                            context.startActivity(Intent(context, OrderHistoryActivity::class.java))
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = stringResource(R.string.order_history_title)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            val currentBottomNavRoute = navBackStackEntry?.destination?.route
            if (bottomNavItems.any { it.route == currentBottomNavRoute}) {
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
                        Text(
                            stringResource(R.string.error_active_order_id_missing),
                            modifier = Modifier.testTag(MENU_SCREEN_MISSING_ORDER_ID_ERROR_TAG) // Added testTag
                        )
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