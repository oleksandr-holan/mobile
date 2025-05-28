package com.example.lab1.util

import android.content.Context
import android.util.Log

/**
 * Helper function to get a string resource by its name (key).
 */
fun getStringResourceByName(context: Context, stringName: String): String {
    return try {
        val resId = context.resources.getIdentifier(stringName, "string", context.packageName)
        if (resId == 0) {
            Log.w("ResourceUtils", "String resource not found for key: $stringName. Returning key as fallback.")
            stringName // Fallback to the key itself if not found
        } else {
            context.getString(resId)
        }
    } catch (e: Exception) {
        Log.e("ResourceUtils", "Error getting string resource for key: $stringName", e)
        stringName // Fallback in case of any exception
    }
} 