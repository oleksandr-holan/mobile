package com.example.lab1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val username: String,
    val passwordHash: String,
    val dateOfBirth: String? = null 
)