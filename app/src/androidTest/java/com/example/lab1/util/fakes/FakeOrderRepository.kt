package com.example.lab1.util.fakes

import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.data.repository.OrderRepository
import com.example.lab1.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeOrderRepository @Inject constructor() : OrderRepository {

    private val _latestActiveOrderFlow = MutableStateFlow<OrderEntity?>(null)
    fun setLatestActiveOrder(order: OrderEntity?) {
        _latestActiveOrderFlow.value = order
    }

    private var nextOrderIdCounter = 1L
    private val ordersMap = mutableMapOf<Long, OrderEntity>()
    private val menuItems = listOf(
        MenuItem("pizza1", "pizza1_name", "pizza1_desc", "12.99", "Pizza", null)
    )

    override fun observeMenuItemsFromApi(): Flow<DataResult<List<MenuItem>>> =
        flowOf(DataResult.Success(menuItems))

    override fun getMenuItemByIdFromApi(itemId: String): Flow<DataResult<MenuItem?>> =
        flowOf(DataResult.Success(menuItems.find { it.id == itemId }))

    override suspend fun createNewOrder(tableNumber: Int): Long {
        val newOrderId = nextOrderIdCounter++
        val newOrder = OrderEntity(
            orderId = newOrderId,
            tableNumber = tableNumber,
            timestamp = Calendar.getInstance().timeInMillis,
            status = "Active"
        )
        ordersMap[newOrderId] = newOrder
        _latestActiveOrderFlow.value = newOrder
        return newOrderId
    }

    override fun getOrderById(orderId: Long): Flow<OrderEntity?> {
        return flowOf(ordersMap[orderId])
    }

    override fun getLatestActiveOrder(): Flow<OrderEntity?> {
        return _latestActiveOrderFlow.asStateFlow()
    }

    override suspend fun updateOrder(order: OrderEntity) {
        ordersMap[order.orderId] = order
        if (order.status == "Active" && order.orderId == _latestActiveOrderFlow.value?.orderId) {
            _latestActiveOrderFlow.value = order
        }
    }

    override suspend fun addOrderItemToOrder(orderItem: OrderItemEntity): Long = 1L
    override suspend fun updateOrderItem(orderItem: OrderItemEntity) {}
    override suspend fun deleteOrderItem(orderItem: OrderItemEntity) {}
    override fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>> =
        flowOf(emptyList())

    override fun getOrderItemById(orderItemId: Long): Flow<OrderItemEntity?> = flowOf(null)

    fun clearAllOrders() {
        ordersMap.clear()
        _latestActiveOrderFlow.value = null
        nextOrderIdCounter = 1L
    }
}