package com.example.lab1.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lab1.ui.navigation.bottomNavItems // Import the list

@Composable
fun AppBottomNavigationBar(navController: NavController) {
    NavigationBar { // Use NavigationBar from Material 3
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    // Prevent navigating to the same screen again
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            // Find the graph's start destination route
                            val startDestination = navController.graph.startDestinationRoute
                            if (startDestination != null) {
                                popUpTo(startDestination) {
                                    saveState = true // Save state of popped screens
                                }
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                alwaysShowLabel = true // Or false depending on your design preference
            )
        }
    }
}