package com.example.lab1.ui.navigation

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lab1.MainActivity
import com.example.lab1.R
import com.example.lab1.data.repository.AuthResult
import com.example.lab1.ui.feature.home.MAIN_APP_SCREEN_TITLE_TAG
import com.example.lab1.ui.feature.home.MENU_SCREEN_MISSING_ORDER_ID_ERROR_TAG
import com.example.lab1.ui.feature.login.waitUntilExists
import com.example.lab1.ui.feature.order.ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG
import com.example.lab1.ui.feature.order.ORDER_SCREEN_FAB_TAG
import com.example.lab1.ui.feature.order.ORDER_SCREEN_NEW_ORDER_BUTTON_TAG
import com.example.lab1.ui.feature.order.ORDER_SCREEN_NO_ACTIVE_ORDER_TEXT_TAG
import com.example.lab1.util.fakes.FakeAuthRepository
import com.example.lab1.util.fakes.FakeOrderRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class OrderToMenuNavigationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeAuthRepository: FakeAuthRepository

    @Inject
    lateinit var fakeOrderRepository: FakeOrderRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        // Ensure logged out initially, so we land on LoginScreen
        // FakeSettingsRepository (in TestRepositoryModule) already handles this.
        fakeAuthRepository.nextAuthResultProvider =
            { _, _ -> AuthResult.Success } // Default to success for login
        fakeOrderRepository.clearAllOrders() // Clear orders before each test
    }

    private fun login() {
        composeTestRule.onNodeWithTag("login_username_input").performTextInput("testuser")
        composeTestRule.onNodeWithTag("login_password_input").performTextInput("password")
        composeTestRule.onNodeWithTag("login_login_button").performClick()
        val expectedMainScreenTitle = composeTestRule.activity.getString(R.string.orders_title)

        // Wait for the "Orders" title to appear, indicating navigation has occurred.
        // Increased timeout as navigation and view composition can take a moment.
        composeTestRule.waitUntilExists(hasTestTag("app_top_bar_title_$expectedMainScreenTitle"), timeoutMillis = 10000)
    }

    @Test
    fun navigateFromOrderScreenToMenuScreen_whenOrderExists_loadsMenuScreen() {
        // 1. Login to get to MainAppScreen (OrderScreen)
        login()

        // 2. Ensure an active order exists or create one.
        //    For this test, let's explicitly create one via UI if none exists,
        //    or rely on FakeOrderRepository to provide one.
        //    Let's simulate creating a new one if "No active order" text is shown.

        // Wait for OrderScreen content to settle
        composeTestRule.waitForIdle() // give viewmodel a chance to load initial order
        Thread.sleep(500) // Extra safety for state to propagate

        val noActiveOrderNode = composeTestRule.onNodeWithTag(
            ORDER_SCREEN_NO_ACTIVE_ORDER_TEXT_TAG, useUnmergedTree = true
        )
        val activeOrderDisplayNode = composeTestRule.onNodeWithTag(
            ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG, useUnmergedTree = true
        )

        if (noActiveOrderNode.isDisplayed()) { // Check if the "No active order" text is there
            composeTestRule.onNodeWithTag(ORDER_SCREEN_NEW_ORDER_BUTTON_TAG).performClick()
            // Wait for the active order text to appear
            composeTestRule.waitUntilExists(
                hasTestTag(ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG), timeoutMillis = 5000
            )
        } else {
            // If "No active order" isn't displayed, assume activeOrderDisplayNode should be.
            // If FakeOrderRepository was set up to provide an order initially, this branch would be taken.
            activeOrderDisplayNode.assertIsDisplayed()
        }

        // At this point, an active order should exist and be displayed
        composeTestRule.onNodeWithTag(ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG).assertIsDisplayed()

        // 3. Click FAB on OrderScreen
        composeTestRule.onNodeWithTag(ORDER_SCREEN_FAB_TAG).performClick()

        // 4. Verify MenuScreen is displayed
        val menuScreenTitle = composeTestRule.activity.getString(R.string.select_menu_items_title)
        val expectedTopBarTitleTag = "app_top_bar_title_$menuScreenTitle" // Construct the correct dynamic tag
        composeTestRule.waitUntilExists(
            hasTestTag(expectedTopBarTitleTag), // Check for the correct tag
            timeoutMillis = 5000
        )
        composeTestRule.onNodeWithTag(expectedTopBarTitleTag) // Use the correct dynamic tag for assertion
            .assertTextEquals(menuScreenTitle)
            .assertIsDisplayed()

        // 5. Verify the "Error: Active Order ID is missing" message is NOT displayed
        composeTestRule.onNodeWithTag(
            MENU_SCREEN_MISSING_ORDER_ID_ERROR_TAG, useUnmergedTree = true
        ).assertDoesNotExist() // Crucial for verifying parameter passing implicitly worked
    }
}

// Helper extension from LoginScreenTest (ensure it's accessible, e.g., in a common test util file)
fun ComposeTestRule.waitUntilExists(
    matcher: SemanticsMatcher, timeoutMillis: Long = 2000L
) {
    this.waitUntil(timeoutMillis) {
        this.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
    }
}