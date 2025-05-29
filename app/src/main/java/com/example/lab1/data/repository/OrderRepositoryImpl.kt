package com.example.lab1.data.repository

import com.example.lab1.data.local.dao.OrderDao
import com.example.lab1.data.local.dao.OrderItemDao
import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.data.remote.MenuApiService
import com.example.lab1.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val menuApiService: MenuApiService
) : OrderRepository {

    override fun observeMenuItemsFromApi(): Flow<DataResult<List<MenuItem>>> = flow {
        emit(DataResult.Loading)
        try {
            val response = menuApiService.getMenuItems()
            if (response.isSuccessful) {
                val dtos = response.body()
                if (dtos != null) {
                    val menuItems = dtos.map { dto -> 
                        MenuItem(
                            id = dto.id,
                            nameKey = dto.nameKey,
                            descriptionKey = dto.descriptionKey,
                            price = dto.price,
                            category = dto.category,
                            imageUrl = dto.imageUrl
                        )
                    }
                    emit(DataResult.Success(menuItems))
                } else {
                    emit(DataResult.Error("api_response_body_null_error"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown API error"
                emit(DataResult.Error("api_call_failed_error: $errorBody"))
            }
        } catch (e: Exception) {
            emit(DataResult.Error("network_exception_error: ${e.message}"))
        }
    }

    override suspend fun createNewOrder(tableNumber: Int): Long {
        val newOrder = OrderEntity(tableNumber = tableNumber, status = "Active")
        return orderDao.insertOrder(newOrder)
    }

    override fun getOrderById(orderId: Long): Flow<OrderEntity?> {
        return orderDao.getOrderById(orderId)
    }

    override fun getLatestActiveOrder(): Flow<OrderEntity?> {
        return orderDao.getLatestActiveOrder()
    }

    override suspend fun updateOrder(order: OrderEntity) {
        orderDao.updateOrder(order)
    }

    override suspend fun addOrderItemToOrder(orderItem: OrderItemEntity): Long {
        return orderItemDao.insertOrderItem(orderItem)
    }

    override suspend fun updateOrderItem(orderItem: OrderItemEntity) {
        orderItemDao.updateOrderItem(orderItem)
    }

    override suspend fun deleteOrderItem(orderItem: OrderItemEntity) {
        orderItemDao.deleteOrderItem(orderItem)
    }

    override fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>> {
        return orderItemDao.getOrderItemsForOrder(orderId)
    }

    override fun getOrderItemById(orderItemId: Long): Flow<OrderItemEntity?> {
        return orderItemDao.getOrderItemById(orderItemId)
    }

    override fun getMenuItemByIdFromApi(itemId: String): Flow<DataResult<MenuItem?>> = flow {
        emit(DataResult.Loading)
        try {
            val response = menuApiService.getMenuItems()
            if (response.isSuccessful) {
                val dtos = response.body()
                if (dtos != null) {
                    val menuItem = dtos.find { it.id == itemId }?.let { dto ->
                        MenuItem(
                            id = dto.id,
                            nameKey = dto.nameKey,
                            descriptionKey = dto.descriptionKey,
                            price = dto.price,
                            category = dto.category,
                            imageUrl = dto.imageUrl
                        )
                    }
                    if (menuItem != null) {
                        emit(DataResult.Success(menuItem))
                    } else {
                        emit(DataResult.Error("menu_item_not_found_in_api_list"))
                    }
                } else {
                    emit(DataResult.Error("api_response_body_null_error_single_item"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown API error"
                emit(DataResult.Error("api_call_failed_error_single_item: $errorBody"))
            }
        } catch (e: Exception) {
            emit(DataResult.Error("network_exception_error_single_item: ${e.message}"))
        }
    }
}