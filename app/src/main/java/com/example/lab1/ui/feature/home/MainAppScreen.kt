package com.example.lab1.ui.feature.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
fun AppNavigationRail(
    navController: NavHostController,
    navItems: List<BottomNavItem>,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier = modifier) {
        navItems.forEach { item ->
            NavigationRailItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationRoute ?: item.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun AppPermanentNavigationDrawer(
    navController: NavHostController,
    navItems: List<BottomNavItem>,
    currentRoute: String?,
    modifier: Modifier = Modifier,
    drawerContent: @Composable () -> Unit
) {
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(
                modifier = modifier,
                drawerContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            ) {
                Spacer(Modifier.height(12.dp))
                navItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(
                                        navController.graph.startDestinationRoute ?: item.route
                                    ) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        content = drawerContent
    )
}


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

    val navigationSuiteLayoutType = when {
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {

            NavigationSuiteType.NavigationDrawer
        }

        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            NavigationSuiteType.NavigationRail
        }

        else -> {
            NavigationSuiteType.NavigationBar
        }
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