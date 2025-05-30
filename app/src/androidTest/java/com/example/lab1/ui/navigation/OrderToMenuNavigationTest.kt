package com.example.lab1.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lab1.MainActivity
import com.example.lab1.R
import com.example.lab1.data.repository.AuthResult
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
        fakeAuthRepository.nextAuthResultProvider = { _, _ -> AuthResult.Success }
        fakeOrderRepository.clearAllOrders()
    }

    private fun login() {
        composeTestRule.onNodeWithTag("login_username_input").performTextInput("testuser")
        composeTestRule.onNodeWithTag("login_password_input").performTextInput("password")
        composeTestRule.onNodeWithTag("login_login_button").performClick()
        val expectedMainScreenTitle = composeTestRule.activity.getString(R.string.orders_title)
        composeTestRule.waitUntilExists(
            hasTestTag("app_top_bar_title_$expectedMainScreenTitle"), timeoutMillis = 10000
        )
    }

    @Test
    fun navigateFromOrderScreenToMenuScreen_whenOrderExists_loadsMenuScreen() {
        login()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val noActiveOrderNode = composeTestRule.onNodeWithTag(
            ORDER_SCREEN_NO_ACTIVE_ORDER_TEXT_TAG, useUnmergedTree = true
        )
        val activeOrderDisplayNode = composeTestRule.onNodeWithTag(
            ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG, useUnmergedTree = true
        )

        if (noActiveOrderNode.isDisplayed()) {
            composeTestRule.onNodeWithTag(ORDER_SCREEN_NEW_ORDER_BUTTON_TAG).performClick()

            composeTestRule.waitUntilExists(
                hasTestTag(ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG), timeoutMillis = 5000
            )
        } else {
            activeOrderDisplayNode.assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ORDER_SCREEN_FAB_TAG).performClick()

        val menuScreenTitle = composeTestRule.activity.getString(R.string.select_menu_items_title)
        val expectedTopBarTitleTag = "app_top_bar_title_$menuScreenTitle"
        composeTestRule.waitUntilExists(
            hasTestTag(expectedTopBarTitleTag), timeoutMillis = 5000
        )
        composeTestRule.onNodeWithTag(expectedTopBarTitleTag).assertTextEquals(menuScreenTitle)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(
            MENU_SCREEN_MISSING_ORDER_ID_ERROR_TAG, useUnmergedTree = true
        ).assertDoesNotExist()
    }
}