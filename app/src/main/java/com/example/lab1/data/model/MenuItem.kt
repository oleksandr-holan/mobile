package com.example.lab1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val category: String,
    val imageUrl: String? = null
)