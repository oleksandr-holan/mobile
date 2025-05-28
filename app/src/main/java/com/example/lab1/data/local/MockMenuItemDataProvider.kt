package com.example.lab1.data.local

import android.content.Context
import com.example.lab1.data.model.MenuItem

object MockMenuItemDataProvider {
    fun getMockMenuItems(context: Context): List<MenuItem> {
        return listOf(
            MenuItem("pizza1", "pizza1_name", "pizza1_desc", "150.00", "Pizza", imageUrl = null),
            MenuItem("pizza2", "pizza2_name", "pizza2_desc", "180.00", "Pizza", imageUrl = null),
            MenuItem("pizza3", "pizza3_name", "pizza3_desc", "170.00", "Pizza", imageUrl = null),
            MenuItem("salad1", "salad1_name", "salad1_desc", "120.00", "Salad", imageUrl = null),
            MenuItem("salad2", "salad2_name", "salad2_desc", "110.00", "Salad", imageUrl = null),
            MenuItem("drink1", "drink1_name", "drink1_desc", "30.00", "Drink", imageUrl = null),
            MenuItem("drink2", "drink2_name", "drink2_desc", "40.00", "Drink", imageUrl = null),
            MenuItem("dessert1", "dessert1_name", "dessert1_desc", "90.00", "Dessert", imageUrl = null)
        )
    }
}