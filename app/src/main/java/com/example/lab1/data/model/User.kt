package com.example.lab1.data.model

data class User(
    val username: String,
    val passwordHash: String,
    val dateOfBirth: String? = null // Nullable, as it's only needed for registration
)