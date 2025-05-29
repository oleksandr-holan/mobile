package com.example.lab1.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MenuItemApiDTO(
    val id: String,
    val nameKey: String,
    val descriptionKey: String,
    val price: String,
    val category: String,
    val imageUrl: String? = null
) 