package com.example.lab1.ui.navigation

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTRATION_ROUTE = "registration"
    const val ORDER_LIST_ROUTE = "order_list"
    const val ARG_ITEM_ID = "orderItemId"
    const val ARG_MENU_ITEM_ID = "menuItemId"
    const val ARG_ACTIVE_ORDER_ID = "activeOrderId"
    const val ADD_ITEM_DETAILS_ROUTE = "add_item_details"
    const val PROFILE_ROUTE = "profile"
    const val MAIN_APP_ROUTE = "main_app"
    const val SETTINGS_ROUTE = "settings"
    const val MENU_SCREEN_ROUTE = "menu_screen"
    const val MENU_SCREEN_WITH_ORDER_ROUTE = "$MENU_SCREEN_ROUTE/{$ARG_ACTIVE_ORDER_ID}"
    const val EDIT_ORDER_ITEM_DETAILS_ROUTE = "$ADD_ITEM_DETAILS_ROUTE/edit/{$ARG_ITEM_ID}"
    const val NEW_ORDER_ITEM_DETAILS_ROUTE =
        "$ADD_ITEM_DETAILS_ROUTE/new/{$ARG_MENU_ITEM_ID}/for_order/{$ARG_ACTIVE_ORDER_ID}"
}