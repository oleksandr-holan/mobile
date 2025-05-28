package com.example.lab1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val tag = "MainActivityLifecycle"

    @Inject 
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate called")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val currentThemeSetting by settingsRepository.appThemeFlow
                .collectAsState(initial = SettingsRepository.DEFAULT_THEME)
            val currentLanguageSetting by settingsRepository.appLanguageFlow
                .collectAsState(initial = SettingsRepository.DEFAULT_LANGUAGE)

            var localeApplied by remember { mutableStateOf(false) }

            LaunchedEffect(currentLanguageSetting) {
                if (currentLanguageSetting.isNotEmpty()) {
                    val locale = Locale(currentLanguageSetting)
                    Locale.setDefault(locale)
                    val config = resources.configuration
                    config.setLocale(locale)
                    @Suppress("DEPRECATION") // resources.updateConfiguration is deprecated but necessary here for immediate effect
                    resources.updateConfiguration(config, resources.displayMetrics)
                    Log.d(tag, "Locale updated to: $currentLanguageSetting, applying to resources.")
                } else {
                    Log.d(tag, "Language setting is empty, using default.")
                }
                localeApplied = true // Signal that locale setup is done
            }

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
                    if (localeApplied) {
                        Log.d(tag, "Locale applied, composing AppNavigationHost")
                        AppNavigationHost(navController = navController)
                    } else {
                        // Optional: Show a loading indicator while locale is being applied
                        // This will be very brief, might not even be visible.
                        Log.d(tag, "Locale not yet applied, showing loading indicator (or nothing)")
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
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
        // It's a good practice to re-check language settings onResume 
        // if you expect changes from other parts of the app or system settings
        // For this basic implementation, we rely on app restart for full effect.
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
        Log.d(tag, "onRestart called") 
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