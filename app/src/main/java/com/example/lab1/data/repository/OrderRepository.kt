package com.example.lab1.data.repository

import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.util.DataResult
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun observeMenuItemsFromApi(): Flow<DataResult<List<MenuItem>>>
    fun getMenuItemByIdFromApi(itemId: String): Flow<DataResult<MenuItem?>>

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