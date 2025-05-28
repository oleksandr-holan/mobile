package com.example.lab1.ui.feature.orderhistory

data class OrderHistoryItem(
    val orderId: String,
    val date: String,
    val totalAmount: String,
    val status: String
) 