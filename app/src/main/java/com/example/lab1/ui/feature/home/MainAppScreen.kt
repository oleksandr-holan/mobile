package com.example.lab1.ui.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.example.lab1.ui.components.AppTopAppBar
import com.example.lab1.ui.feature.item.AddItemDetailsScreen
import com.example.lab1.ui.feature.order.OrderScreen
import com.example.lab1.ui.feature.profile.ProfileScreen
import com.example.lab1.ui.navigation.AppDestinations
import com.example.lab1.ui.navigation.BottomNavItem
import com.example.lab1.ui.navigation.bottomNavItems

@Composable
fun MainAppScreen(
    outerNavController: NavHostController,
    windowSizeClass: WindowSizeClass
) {
    val innerNavController: NavHostController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentScreenTitle = remember(currentRoute) {
        when {
            currentRoute == BottomNavItem.Orders.route -> BottomNavItem.Orders.title
            currentRoute == BottomNavItem.Profile.route -> BottomNavItem.Profile.title
            currentRoute?.startsWith(AppDestinations.ADD_ITEM_DETAILS_ROUTE) == true -> "Item Details"
            else -> bottomNavItems.find { it.route == currentRoute }?.title ?: "App"
        }
    }
    val canNavigateBack = remember(currentRoute) {

        bottomNavItems.none { it.route == currentRoute } && currentRoute != null
    }

    val determinedLayoutType = when {
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> NavigationSuiteType.NavigationDrawer
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteType.NavigationBar
    }


    NavigationSuiteScaffold(
        navigationSuiteItems = {
            bottomNavItems.forEach { screen ->
                item(
                    selected = currentRoute == screen.route,
                    onClick = {
                        if (currentRoute != screen.route) {
                            innerNavController.navigate(screen.route) {
                                popUpTo(innerNavController.graph.startDestinationRoute!!) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                    label = { Text(screen.title) }
                )
            }
        },
        layoutType = determinedLayoutType
    ) {
        Scaffold(
            topBar = {
                AppTopAppBar(
                    title = currentScreenTitle,
                    canNavigateBack = canNavigateBack,
                    onNavigateBack = { innerNavController.popBackStack() }
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = innerNavController,
                startDestination = BottomNavItem.Orders.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                composable(BottomNavItem.Orders.route) {
                    OrderScreen(
                        onNavigateToAddItem = { itemId ->
                            innerNavController.navigate(
                                "${AppDestinations.ADD_ITEM_DETAILS_ROUTE}/$itemId"
                            )
                        },
                        windowSizeClass = windowSizeClass
                    )
                }
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(
                        onNavigateToLogin = {
                            outerNavController.navigate(AppDestinations.LOGIN_ROUTE) {
                                popUpTo(AppDestinations.MAIN_APP_ROUTE) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },

                        )
                }
                composable(
                    route = AppDestinations.ADD_ITEM_DETAILS_WITH_ID_ROUTE,
                    arguments = listOf(
                        navArgument(AppDestinations.ARG_ITEM_ID) { type = NavType.StringType }
                    )
                ) {
                    AddItemDetailsScreen(
                        onNavigateBack = { innerNavController.popBackStack() }

                    )
                }
            }
        }
    }
}