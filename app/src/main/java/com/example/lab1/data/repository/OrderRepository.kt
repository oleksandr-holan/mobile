package com.example.lab1.data.repository

import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.util.DataResult
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    // fun getAllMenuItems(): Flow<List<MenuItem>> // Removed
    // fun getMenuItemById(itemId: String): Flow<MenuItem?> // Potentially re-add if needed for domain object post-API
    // suspend fun addMenuItem(menuItem: MenuItem) // Removed
    // suspend fun deleteMenuItem(itemId: String) // Removed
    // suspend fun getMenuItemCount(): Int  // Removed
    // suspend fun clearAndRepopulateMenuItems() // Removed

    fun observeMenuItemsFromApi(): Flow<DataResult<List<MenuItem>>>
    fun getMenuItemByIdFromApi(itemId: String): Flow<DataResult<MenuItem?>> // Added for fetching single item from API if needed for details screen

    suspend fun createNewOrder(tableNumber: Int): Long
    fun getOrderById(orderId: Long): Flow<OrderEntity?>
    fun getLatestActiveOrder(): Flow<OrderEntity?>
    suspend fun updateOrder(order: OrderEntity)

    suspend fun addOrderItemToOrder(orderItem: OrderItemEntity): Long
    suspend fun updateOrderItem(orderItem: OrderItemEntity)
    suspend fun deleteOrderItem(orderItem: OrderItemEntity)
    fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>>
    fun getOrderItemById(orderItemId: Long): Flow<OrderItemEntity?>
}