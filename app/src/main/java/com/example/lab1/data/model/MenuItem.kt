package com.example.lab1.data.model

// Represents a menu item
data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: String, // Keep as String for display simplicity, consider BigDecimal for real apps
    val category: String // e.g., "Pizza", "Salad", "Drink"
)