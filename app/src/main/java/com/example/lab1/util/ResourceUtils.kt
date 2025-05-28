package com.example.lab1.util

import android.content.Context
import android.util.Log
import com.example.lab1.R

fun getStringResourceForKey(context: Context, key: String): String {
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
            context.getString(resId)
        } catch (e: Exception) {
            Log.e("ResourceUtils", "Error getting string resource for ID: $resId from key: $key", e)
            key // Fallback to key if getString fails for a valid ID (should not happen)
        }
    } else {
        Log.w("ResourceUtils", "Unknown string resource key: $key. Returning key as fallback.")
        key // Fallback to the key itself if not found
    }
} 