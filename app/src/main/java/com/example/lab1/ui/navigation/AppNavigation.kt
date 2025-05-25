package com.example.lab1.ui.navigation

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTRATION_ROUTE = "registration"
    const val ORDER_LIST_ROUTE = "order_list" 
    const val ARG_ITEM_ID = "itemId"
    const val ADD_ITEM_DETAILS_ROUTE = "add_item_details"
    const val PROFILE_ROUTE = "profile"
    const val MAIN_APP_ROUTE = "main_app"
    const val ADD_ITEM_DETAILS_WITH_ID_ROUTE = "$ADD_ITEM_DETAILS_ROUTE/{$ARG_ITEM_ID}"
    const val SETTINGS_ROUTE = "settings"
}