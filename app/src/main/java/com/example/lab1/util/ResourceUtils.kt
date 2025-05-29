package com.example.lab1.util

import android.content.Context
import android.util.Log
import com.example.lab1.R
import java.util.Locale

fun getStringResourceForKey(context: Context, key: String): String {
    val desiredLocale = Locale.getDefault()
    var localizedContext = context
    val currentConfiguration = context.resources.configuration

    val primaryLocaleInContext = if (currentConfiguration.locales.isEmpty) null else currentConfiguration.locales[0]

    if (primaryLocaleInContext != desiredLocale) {
        Log.d("ResourceUtils", "Context locale (${primaryLocaleInContext}) doesn\'t match default (${desiredLocale}). Creating new localized context for key: $key")
        val config = android.content.res.Configuration(currentConfiguration)
        config.setLocale(desiredLocale)
        localizedContext = context.createConfigurationContext(config)
    }

    val resId = when (key) {
        "pizza1_name" -> R.string.pizza1_name
        "pizza1_desc" -> R.string.pizza1_desc
        "pizza2_name" -> R.string.pizza2_name
        "pizza2_desc" -> R.string.pizza2_desc
        "pizza3_name" -> R.string.pizza3_name
        "pizza3_desc" -> R.string.pizza3_desc
        "salad1_name" -> R.string.salad1_name
        "salad1_desc" -> R.string.salad1_desc
        "salad2_name" -> R.string.salad2_name
        "salad2_desc" -> R.string.salad2_desc
        "drink1_name" -> R.string.drink1_name
        "drink1_desc" -> R.string.drink1_desc
        "drink2_name" -> R.string.drink2_name
        "drink2_desc" -> R.string.drink2_desc
        "dessert1_name" -> R.string.dessert1_name
        "dessert1_desc" -> R.string.dessert1_desc
        else -> 0
    }

    return if (resId != 0) {
        try {
            localizedContext.getString(resId)
        } catch (e: Exception) {
            Log.e("ResourceUtils", "Error getting string resource for ID: $resId from key: $key (locale: ${desiredLocale.toLanguageTag()})", e)
            key
        }
    } else {
        Log.w("ResourceUtils", "Unknown string resource key: $key. Returning key as fallback.")
        key
    }
} 