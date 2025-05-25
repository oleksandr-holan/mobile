package com.example.lab1.data.repository

import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getAllMenuItems(): Flow<List<MenuItem>>
    fun getMenuItemById(itemId: String): Flow<MenuItem?>
    suspend fun addMenuItem(menuItem: MenuItem) 
    suspend fun deleteMenuItem(itemId: String) 
    suspend fun getMenuItemCount(): Int 

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