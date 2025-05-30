package com.example.lab1.ui.feature.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lab1.MainActivity
import com.example.lab1.R
import com.example.lab1.data.repository.AuthResult
import com.example.lab1.util.fakes.FakeAuthRepository
// import com.example.lab1.util.fakes.FakeSettingsRepository // Not directly needed here, but TestRepositoryModule handles it
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LoginScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeAuthRepository: FakeAuthRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        // FakeSettingsRepository (provided by TestRepositoryModule) ensures logged-out state,
        // so MainActivity starts on LoginScreen.
    }

    @Test
    fun loginScreen_initialElements_areDisplayed() {
        composeTestRule.waitUntilExists(hasText(composeTestRule.activity.getString(R.string.login_title)), timeoutMillis = 5000)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.login_title)).assertIsDisplayed()

        composeTestRule.onNodeWithTag("login_username_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_username_input").assert(hasSetTextAction())

        composeTestRule.onNodeWithTag("login_password_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_password_input").assert(hasSetTextAction())

        composeTestRule.onNodeWithTag("login_login_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_login_button").assertHasClickAction()

        composeTestRule.onNodeWithTag("login_register_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_register_button").assertHasClickAction()
    }

    @Test
    fun loginScreen_successfulLogin_navigatesToMainAppScreen() {
        // Configure fake repository for a successful login with no delay
        fakeAuthRepository.loginDelay = 0L
        fakeAuthRepository.nextAuthResultProvider = { _, _ -> AuthResult.Success }

        val usernameToType = "testuser"
        val passwordToType = "password123"

        // Input Username
        composeTestRule.onNodeWithTag("login_username_input").performTextInput(usernameToType)
        composeTestRule.onNodeWithTag("login_username_input").assert(hasText(usernameToType))

        // Input Password
        composeTestRule.onNodeWithTag("login_password_input").performTextInput(passwordToType)
        // Password field might not directly show text if visual transformation is on,
        // but performTextInput works. We can check if the button becomes enabled.

        // Click Login Button
        // The button should be enabled now since both fields have text (ViewModel logic)
        composeTestRule.onNodeWithTag("login_login_button").assertIsEnabled()
        composeTestRule.onNodeWithTag("login_login_button").performClick()

        // After successful login and navigation, MainAppScreen shows OrderScreen by default.
        // The AppTopAppBar in MainAppScreen should display "Orders" as the title.
        val expectedMainScreenTitle = composeTestRule.activity.getString(R.string.orders_title)

        // Wait for the "Orders" title to appear, indicating navigation has occurred.
        // Increased timeout as navigation and view composition can take a moment.
        composeTestRule.waitUntilExists(hasTestTag("app_top_bar_title_$expectedMainScreenTitle"), timeoutMillis = 10000)
        composeTestRule.onNodeWithTag("app_top_bar_title_$expectedMainScreenTitle").assertIsDisplayed()

        // As an additional check, the login screen's title should no longer be present
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.login_title))
            .assertDoesNotExist()
    }
}

// Helper extension for waiting until a node with specific text exists
fun ComposeTestRule.waitUntilExists(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 2000L
) {
    this.waitUntil(timeoutMillis) {
        this.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
    }
}