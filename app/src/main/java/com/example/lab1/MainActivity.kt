package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab1.ui.feature.item.AddItemDetailsScreen
import com.example.lab1.ui.feature.login.LoginScreen
import com.example.lab1.ui.feature.order.view.OrderScreen
import com.example.lab1.ui.feature.register.RegistrationScreen
import com.example.lab1.ui.navigation.AppDestinations
import com.example.lab1.ui.theme.Lab1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Lab1Theme {
                // Create the NavController
                val navController: NavHostController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Setup the NavHost
                    AppNavigationHost(navController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        // Set the start destination (usually login)
        startDestination = AppDestinations.LOGIN_ROUTE
    ) {
        // Define composable for the Login screen
        composable(route = AppDestinations.LOGIN_ROUTE) {
            // Pass navController for navigation actions
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to order list on success
                    // Clear back stack up to login to prevent going back
                    navController.navigate(AppDestinations.ORDER_LIST_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                        launchSingleTop = true // Avoid multiple copies of order list
                    }
                },
                onNavigateToRegister = {
                    // Navigate to registration screen
                    navController.navigate(AppDestinations.REGISTRATION_ROUTE)
                }
            )
        }

        // Define composable for the Registration screen
        composable(route = AppDestinations.REGISTRATION_ROUTE) {
            // Pass navController for navigation actions
            RegistrationScreen(
                onRegistrationSuccess = {
                    // Navigate to order list on success
                    // Clear back stack up to login to prevent going back
                    navController.navigate(AppDestinations.ORDER_LIST_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateBackToLogin = {
                    // Navigate back to login
                    navController.popBackStack() // Simple back navigation
                }
            )
        }

        // Define composable for the Order List screen
        composable(route = AppDestinations.ORDER_LIST_ROUTE) {
            OrderScreen(
                onNavigateToAddItem = { /* Define action later if needed */ }
                // We might add navigation to AddItemDetails from here later
            )
        }

        // Define composable for the Add Item Details screen (example)
        // This might need arguments later, but keep it simple for now
        composable(route = AppDestinations.ADD_ITEM_DETAILS_ROUTE) {
            // Example: Get item name argument if passed (we'll add this later)
            val itemName = "Placeholder Item" // Replace with argument retrieval later
            AddItemDetailsScreen(
                itemName = itemName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}