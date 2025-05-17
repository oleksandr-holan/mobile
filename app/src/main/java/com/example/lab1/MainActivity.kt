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
import com.example.lab1.ui.feature.home.MainAppScreen
import com.example.lab1.ui.feature.login.LoginScreen
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
        startDestination = AppDestinations.LOGIN_ROUTE
    ) {
        composable(route = AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to the Main App Screen (with Scaffold/BottomNav)
                    navController.navigate(AppDestinations.MAIN_APP_ROUTE) {
                        // Clear the back stack up to the start destination (login)
                        popUpTo(navController.graph.startDestinationRoute!!) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTRATION_ROUTE)
                }
            )
        }

        composable(route = AppDestinations.REGISTRATION_ROUTE) {
            RegistrationScreen(
                onRegistrationSuccess = {
                    // Navigate to the Main App Screen after registration too
                    navController.navigate(AppDestinations.MAIN_APP_ROUTE) {
                        popUpTo(navController.graph.startDestinationRoute!!) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Define the composable for the Main App Screen itself
        composable(route = AppDestinations.MAIN_APP_ROUTE) {
            MainAppScreen(outerNavController = navController) // Pass the main NavController
        }

        // Note: OrderScreen, HistoryScreen, AddItemDetailsScreen are now
        // handled by the *inner* NavHost within MainAppScreen.
        // We don't define them directly here anymore unless they are
        // accessible *outside* the logged-in state (which is unlikely).
    }
}