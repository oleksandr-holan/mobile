package com.example.lab1.ui.feature.login

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
    lateinit var fakeAuthRepository: FakeAuthRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun loginScreen_initialElements_areDisplayed() {
        composeTestRule.waitUntilExists(
            hasText(composeTestRule.activity.getString(R.string.login_title)),
            timeoutMillis = 5000
        )
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.login_title))
            .assertIsDisplayed()

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

        fakeAuthRepository.loginDelay = 0L
        fakeAuthRepository.nextAuthResultProvider = { _, _ -> AuthResult.Success }

        val usernameToType = "testuser"
        val passwordToType = "password123"

        composeTestRule.onNodeWithTag("login_username_input").performTextInput(usernameToType)
        composeTestRule.onNodeWithTag("login_username_input").assert(hasText(usernameToType))

        composeTestRule.onNodeWithTag("login_password_input").performTextInput(passwordToType)

        composeTestRule.onNodeWithTag("login_login_button").assertIsEnabled()
        composeTestRule.onNodeWithTag("login_login_button").performClick()

        val expectedMainScreenTitle = composeTestRule.activity.getString(R.string.orders_title)
        composeTestRule.waitUntilExists(
            hasTestTag("app_top_bar_title_$expectedMainScreenTitle"),
            timeoutMillis = 10000
        )
        composeTestRule.onNodeWithTag("app_top_bar_title_$expectedMainScreenTitle")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.login_title))
            .assertDoesNotExist()
    }
}


fun ComposeTestRule.waitUntilExists(
    matcher: SemanticsMatcher, timeoutMillis: Long = 2000L
) {
    this.waitUntil(timeoutMillis) {
        this.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
    }
}