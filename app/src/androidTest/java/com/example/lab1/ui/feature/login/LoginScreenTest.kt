package com.example.lab1.ui.feature.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lab1.MainActivity
import com.example.lab1.R
import com.example.lab1.data.repository.AuthResult
import com.example.lab1.util.fakes.FakeAuthRepository
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
    lateinit var fakeAuthRepository: FakeAuthRepository // To control login behavior

    @Before
    fun setUp() {
        hiltRule.inject()
        // Ensure MainActivity starts on LoginScreen by default with FakeSettingsRepository
        // (which is set to logged-out state)
    }

    @Test
    fun loginScreen_initialElements_areDisplayed() {
        // Verify Login Title (optional, but good for context)
        // We need to wait for UI to settle, especially if there's navigation logic
        composeTestRule.waitUntilExists(hasText(composeTestRule.activity.getString(R.string.login_title)), timeoutMillis = 5000)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.login_title)).assertIsDisplayed()

        // Verify Username Input
        composeTestRule.onNodeWithTag("login_username_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_username_input")
            .assert(hasSetTextAction()) // Checks if it's an input field

        // Verify Password Input
        composeTestRule.onNodeWithTag("login_password_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_password_input")
            .assert(hasSetTextAction())

        // Verify Login Button
        composeTestRule.onNodeWithTag("login_login_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_login_button").assertHasClickAction()

        // Verify Register Button/Text
        composeTestRule.onNodeWithTag("login_register_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_register_button").assertHasClickAction()
    }

    @Test
    fun loginScreen_inputAndLoginAttempt_showsLoading() {
        // Configure fake repository for this test
        fakeAuthRepository.loginDelay = 1000 // Simulate network latency
        fakeAuthRepository.nextAuthResultProvider = { _, _ ->
            // Keep it pending to show loading, or return success if we want to test navigation
            AuthResult.Success // Let's assume it would eventually succeed
        }

        // Input Username
        composeTestRule.onNodeWithTag("login_username_input").performTextInput("testuser")
        composeTestRule.onNodeWithTag("login_username_input").assert(hasText("testuser"))

        // Input Password
        composeTestRule.onNodeWithTag("login_password_input").performTextInput("password123")
        composeTestRule.onNodeWithTag("login_password_input").assert(hasText("password123"))

        // Click Login Button
        // The button should be enabled now since both fields have text
        composeTestRule.onNodeWithTag("login_login_button").assertIsEnabled()
        composeTestRule.onNodeWithTag("login_login_button").performClick()

        // Verify loading indicator is shown (inside the button)
        // It replaces the button's text content.
        composeTestRule.onNodeWithTag("login_loading_indicator", useUnmergedTree = true).assertIsDisplayed()

        // Optionally, verify the "Login" text is gone from the button
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.login_button_text))
            .assertDoesNotExist() // This text should be replaced by the CircularProgressIndicator

        // To make the test complete and not leave the app in a loading state indefinitely for subsequent tests
        // (if any were to run in the same activity instance, though typically they don't):
        // We can wait for the loading to "finish" if we were testing navigation.
        // For just showing loading, this is sufficient.
        // If testing navigation, you'd wait for the next screen.
        // composeTestRule.waitForIdle() // Allow coroutines to progress
    }
}

fun ComposeTestRule.waitUntilExists(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 2000L // Default timeout, can be overridden
) {
    this.waitUntil(timeoutMillis) { // Now 'this' explicitly refers to ComposeTestRule
        this.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
    }
}