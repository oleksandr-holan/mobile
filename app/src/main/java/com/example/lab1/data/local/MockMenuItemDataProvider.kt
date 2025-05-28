package com.example.lab1.data.local

import android.content.Context
import com.example.lab1.data.model.MenuItem
import com.example.lab1.R

object MockMenuItemDataProvider {
    fun getMockMenuItems(context: Context): List<MenuItem> {
        return listOf(
            MenuItem("pizza1", context.getString(R.string.pizza1_name), context.getString(R.string.pizza1_desc), "150.00", "Pizza", imageUrl = null),
            MenuItem("pizza2", context.getString(R.string.pizza2_name), context.getString(R.string.pizza2_desc), "180.00", "Pizza", imageUrl = null),
            MenuItem("pizza3", context.getString(R.string.pizza3_name), context.getString(R.string.pizza3_desc), "170.00", "Pizza", imageUrl = null),
            MenuItem("salad1", context.getString(R.string.salad1_name), context.getString(R.string.salad1_desc), "120.00", "Salad", imageUrl = null),
            MenuItem("salad2", context.getString(R.string.salad2_name), context.getString(R.string.salad2_desc), "110.00", "Salad", imageUrl = null),
            MenuItem("drink1", context.getString(R.string.drink1_name), context.getString(R.string.drink1_desc), "30.00", "Drink", imageUrl = null),
            MenuItem("drink2", context.getString(R.string.drink2_name), context.getString(R.string.drink2_desc), "40.00", "Drink", imageUrl = null),
            MenuItem("dessert1", context.getString(R.string.dessert1_name), context.getString(R.string.dessert1_desc), "90.00", "Dessert", imageUrl = null)
        )
    }
}