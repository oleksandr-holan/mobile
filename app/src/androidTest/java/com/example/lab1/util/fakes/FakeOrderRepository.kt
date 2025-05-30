package com.example.lab1.util.fakes

import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.data.repository.OrderRepository
import com.example.lab1.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeOrderRepository @Inject constructor() : OrderRepository {
    override fun observeMenuItemsFromApi(): Flow<DataResult<List<MenuItem>>> = flowOf(DataResult.Success(emptyList()))
    override fun getMenuItemByIdFromApi(itemId: String): Flow<DataResult<MenuItem?>> = flowOf(DataResult.Success(null))
    override suspend fun createNewOrder(tableNumber: Int): Long = 1L
    override fun getOrderById(orderId: Long): Flow<OrderEntity?> = flowOf(null)
    override fun getLatestActiveOrder(): Flow<OrderEntity?> = flowOf(null)
    override suspend fun updateOrder(order: OrderEntity) {}
    override suspend fun addOrderItemToOrder(orderItem: OrderItemEntity): Long = 1L
    override suspend fun updateOrderItem(orderItem: OrderItemEntity) {}
    override suspend fun deleteOrderItem(orderItem: OrderItemEntity) {}
    override fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>> = flowOf(emptyList())
    override fun getOrderItemById(orderItemId: Long): Flow<OrderItemEntity?> = flowOf(null)
}