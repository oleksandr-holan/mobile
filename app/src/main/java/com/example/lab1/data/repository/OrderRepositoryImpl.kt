package com.example.lab1.data.repository

import com.example.lab1.data.local.dao.MenuItemDao
import com.example.lab1.data.local.dao.OrderDao
import com.example.lab1.data.local.dao.OrderItemDao
import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val menuItemDao: MenuItemDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) : OrderRepository {

    override fun getAllMenuItems(): Flow<List<MenuItem>> {
        return menuItemDao.getAllMenuItems()
    }

    override fun getMenuItemById(itemId: String): Flow<MenuItem?> {
        return menuItemDao.getMenuItemById(itemId)
    }

    override suspend fun addMenuItem(menuItem: MenuItem) {
        menuItemDao.insertMenuItem(menuItem)
    }

    override suspend fun deleteMenuItem(itemId: String) {
        menuItemDao.deleteMenuItemById(itemId)
    }

    override suspend fun getMenuItemCount(): Int {
        return menuItemDao.getMenuItemsCount()
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
}