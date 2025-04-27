package com.example.waiterapp

// Simple data class to hold user information
data class User(
    val username: String,
    // In a real app, never store passwords directly! Use hashing.
    val passwordHash: String, // Renamed for clarity, still storing plain text for lab simplicity
    val dateOfBirth: String? = null // Nullable, as it's only needed for registration
)