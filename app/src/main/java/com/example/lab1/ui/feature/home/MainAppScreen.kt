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
import com.example.lab1.ui.feature.item.AddItemDetailsScreen
import com.example.lab1.ui.feature.order.OrderScreen
import com.example.lab1.ui.feature.profile.ProfileScreen

@Composable
fun MainAppScreen(outerNavController: NavHostController) {
    val innerNavController: NavHostController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreenTitle = remember(currentRoute) {
        when (currentRoute) {
            BottomNavItem.Orders.route -> BottomNavItem.Orders.title
            BottomNavItem.Profile.route -> BottomNavItem.Profile.title
            AppDestinations.ADD_ITEM_DETAILS_ROUTE -> "Item Details" 
            else -> "" 
        }
    }
    val canNavigateBack = remember(currentRoute) {
        bottomNavItems.none { it.route == currentRoute } && currentRoute != null
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = currentScreenTitle,
                canNavigateBack = canNavigateBack,
                onNavigateBack = { innerNavController.popBackStack() } 
            )
        },
        bottomBar = { AppBottomNavigationBar(navController = innerNavController) }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = BottomNavItem.Orders.route, 
            modifier = Modifier.padding(innerPadding) 
        ) {
            composable(BottomNavItem.Orders.route) {
                OrderScreen(
                    
                    onNavigateToAddItem = { itemId -> 
                        innerNavController.navigate(
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
                            } 
                            launchSingleTop = true
                        }
                    }
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