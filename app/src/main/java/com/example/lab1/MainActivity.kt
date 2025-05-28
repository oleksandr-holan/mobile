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
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import kotlinx.coroutines.flow.first

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
            val persistedLanguageSetting by settingsRepository.appLanguageFlow
                .collectAsState(initial = null)

            var languageToApplyForUI by remember { mutableStateOf<String?>(null) }
            var startDestination by remember { mutableStateOf<String?>(null) }

            val navController: NavHostController = rememberNavController()
            val innerNavController: NavHostController = rememberNavController()

            LaunchedEffect(key1 = Unit) {
                val loggedInUser = settingsRepository.loggedInUserUsernameFlow.first()
                startDestination = if (loggedInUser != null && loggedInUser.isNotBlank()) {
                    Log.d(tag, "User '$loggedInUser' is logged in. Navigating to Main App.")
                    AppDestinations.MAIN_APP_ROUTE
                } else {
                    Log.d(tag, "No user logged in. Navigating to Login.")
                    AppDestinations.LOGIN_ROUTE
                }
            }

            LaunchedEffect(persistedLanguageSetting) {
                if (persistedLanguageSetting == null) {
                    Log.d(tag, "[[LAUNCHED_EFFECT_LANG]] Waiting for DataStore to emit a language setting...")
                    return@LaunchedEffect
                }

                val targetLanguage = if (persistedLanguageSetting!!.isNotEmpty()) {
                    persistedLanguageSetting
                } else {
                    SettingsRepository.DEFAULT_LANGUAGE
                }
                Log.d(tag, "[[LAUNCHED_EFFECT_LANG]] Target language determined: $targetLanguage (from persisted: '$persistedLanguageSetting')")

                val locale = Locale(targetLanguage)
                val config = resources.configuration
                val currentActivityLocale = Locale(config.locales[0].toLanguageTag())

                if (currentActivityLocale != locale || Locale.getDefault() != locale) {
                    Locale.setDefault(locale)
                    config.setLocale(locale)
                    @Suppress("DEPRECATION")
                    resources.updateConfiguration(config, resources.displayMetrics)
                    Log.d(tag, "[[LAUNCHED_EFFECT_LANG]] Locale updated. Default: ${Locale.getDefault()}, Resources: $locale. From old: $currentActivityLocale")
                } else {
                    Log.d(tag, "[[LAUNCHED_EFFECT_LANG]] Locale $locale already effectively set.")
                }
                languageToApplyForUI = targetLanguage
            }

            if (languageToApplyForUI == null || startDestination == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Log.d(tag, "Waiting for language ($languageToApplyForUI) or start destination ($startDestination), showing loading indicator.")
                    CircularProgressIndicator()
                }
            } else {
                key(languageToApplyForUI) { 
                    Log.d(tag, "[[KEY_BLOCK_LANG]] Recomposing with language: $languageToApplyForUI. Current Locale.getDefault(): ${Locale.getDefault().language}")
                    val useDarkTheme = when (currentThemeSetting) {
                        "Dark" -> true
                        "Light" -> false
                        else -> isSystemInDarkTheme()
                    }
                    Lab1Theme(darkTheme = useDarkTheme) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Log.d(tag, "[[SURFACE_IN_KEY_BLOCK]] AppNavigationHost to be composed. Start: $startDestination, Language: $languageToApplyForUI. Locale.getDefault(): ${Locale.getDefault().language}")
                            AppNavigationHost(outerNavController = navController, innerNavController = innerNavController, startDestination = startDestination!!)
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
fun AppNavigationHost(outerNavController: NavHostController, innerNavController: NavHostController, startDestination: String) {
    Log.d("AppNavigationHost", "Composing with startDestination: $startDestination. Current Locale.getDefault(): ${Locale.getDefault().language}. LoginScreen title res ID: ${R.string.login_title}")
    NavHost(
        navController = outerNavController,
        startDestination = startDestination
    ) {
        composable(route = AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onLoginSuccess = {
                    outerNavController.navigate(AppDestinations.MAIN_APP_ROUTE) {
                        popUpTo(outerNavController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    outerNavController.navigate(AppDestinations.REGISTRATION_ROUTE)
                }
            )
        }

        composable(route = AppDestinations.REGISTRATION_ROUTE) {
            RegistrationScreen(
                onRegistrationSuccess = {
                    outerNavController.navigate(AppDestinations.MAIN_APP_ROUTE) {
                        popUpTo(outerNavController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateBackToLogin = {
                    outerNavController.popBackStack()
                }
            )
        }

        composable(route = AppDestinations.MAIN_APP_ROUTE) {
            MainAppScreen(outerNavController = outerNavController, innerNavController = innerNavController)
        }
    }
}