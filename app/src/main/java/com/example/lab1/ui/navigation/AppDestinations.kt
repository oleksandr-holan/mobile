package com.example.lab1.ui.navigation

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTRATION_ROUTE = "registration"
     const val ORDER_LIST_ROUTE = "order_list" // This is BottomNavItem.Orders.route now

    const val ARG_ITEM_ID = "orderItemId" // Used for AddItemDetailsScreen when editing OrderItem (Long)
    const val ARG_MENU_ITEM_ID = "menuItemId" // Used for AddItemDetailsScreen when adding new from Menu (String)
    const val ARG_ACTIVE_ORDER_ID = "activeOrderId" // ID of the current order to add item to (Long)

    const val ADD_ITEM_DETAILS_ROUTE = "add_item_details" // Base for details screen
    const val PROFILE_ROUTE = "profile"
    const val MAIN_APP_ROUTE = "main_app"
    const val SETTINGS_ROUTE = "settings"

    const val MENU_SCREEN_ROUTE = "menu_screen"
    // MenuScreen route will now take activeOrderId if an order is active
    const val MENU_SCREEN_WITH_ORDER_ROUTE = "$MENU_SCREEN_ROUTE/{$ARG_ACTIVE_ORDER_ID}"

    // Route for AddItemDetails when EDITING an existing OrderItem
    const val EDIT_ORDER_ITEM_DETAILS_ROUTE = "$ADD_ITEM_DETAILS_ROUTE/edit/{$ARG_ITEM_ID}"

    // Route for AddItemDetails when ADDING a new OrderItem from the Menu
    // It needs both menuItemId (String) and activeOrderId (Long)
    const val NEW_ORDER_ITEM_DETAILS_ROUTE = "$ADD_ITEM_DETAILS_ROUTE/new/{$ARG_MENU_ITEM_ID}/for_order/{$ARG_ACTIVE_ORDER_ID}"
}