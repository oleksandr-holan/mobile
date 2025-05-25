package com.example.lab1.ui.navigation

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTRATION_ROUTE = "registration"
     const val ORDER_LIST_ROUTE = "order_list" // This is BottomNavItem.Orders.route now
    const val ARG_ITEM_ID = "itemId" // Used for AddItemDetailsScreen when editing OrderItem
    const val ARG_MENU_ITEM_ID = "menuItemId" // Used for AddItemDetailsScreen when adding new from Menu
    const val ADD_ITEM_DETAILS_ROUTE = "add_item_details"
    const val PROFILE_ROUTE = "profile"
    const val MAIN_APP_ROUTE = "main_app"

    // Route for AddItemDetails when editing an existing OrderItem
    const val EDIT_ORDER_ITEM_DETAILS_ROUTE = "$ADD_ITEM_DETAILS_ROUTE/edit/{$ARG_ITEM_ID}"

    // Route for AddItemDetails when adding a new OrderItem from the Menu
    const val NEW_ORDER_ITEM_DETAILS_ROUTE = "$ADD_ITEM_DETAILS_ROUTE/new/{$ARG_MENU_ITEM_ID}"

    const val SETTINGS_ROUTE = "settings"
    const val MENU_SCREEN_ROUTE = "menu_screen" // New Route
}