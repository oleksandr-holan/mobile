package com.example.lab1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab1.data.repository.SettingsRepository
import com.example.lab1.ui.feature.home.MainAppScreen
import com.example.lab1.ui.feature.login.LoginScreen
import com.example.lab1.ui.feature.register.RegistrationScreen
import com.example.lab1.ui.navigation.AppDestinations
import com.example.lab1.ui.theme.Lab1Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val tag = "MainActivityLifecycle"

    @Inject // Hilt will inject this
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate called")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val currentThemeSetting by settingsRepository.appThemeFlow
                .collectAsState(initial = SettingsRepository.DEFAULT_THEME)

            val useDarkTheme = when (currentThemeSetting) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            Lab1Theme(darkTheme = useDarkTheme) {
                val navController: NavHostController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    AppNavigationHost(navController = navController)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(tag, "onRestart called") // Good to have for completeness
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy called")
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

                    navController.navigate(AppDestinations.MAIN_APP_ROUTE) {

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

        composable(route = AppDestinations.MAIN_APP_ROUTE) {
            MainAppScreen(outerNavController = navController)
        }
    }
}