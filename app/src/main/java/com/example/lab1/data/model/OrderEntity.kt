package com.example.lab1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val tableNumber: Int,
    val timestamp: Long = Calendar.getInstance().timeInMillis,
    val status: String = "Active"
)