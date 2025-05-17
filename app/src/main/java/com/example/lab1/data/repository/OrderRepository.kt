package com.example.lab1.data.repository

import com.example.lab1.data.model.MenuItem
import com.example.lab1.util.DataResult
import kotlinx.coroutines.delay


interface OrderRepository {
    suspend fun getMenuItems(category: String? = null): DataResult<List<MenuItem>>
    suspend fun getMenuItemDetails(itemId: String): DataResult<MenuItem?>
    // Add other methods like:
    // suspend fun addItemToOrder(tableId: String, itemId: String, quantity: Int, notes: String): Result<Boolean>
    // suspend fun getCurrentOrder(tableId: String): Result<List<OrderItem>>
}

class MockOrderRepository : OrderRepository {

    private val allMenuItems = listOf(
        MenuItem("pizza1", "Піца Маргарита", "Класична піца з томатним соусом, моцарелою та базиліком.", "150 грн", "Pizza"),
        MenuItem("pizza2", "Піца Пепероні", "Гостра піца з салямі пепероні та моцарелою.", "180 грн", "Pizza"),
        MenuItem("salad1", "Салат Цезар", "Салат з куркою, грінками, пармезаном та соусом Цезар.", "120 грн", "Salad"),
        MenuItem("salad2", "Грецький Салат", "Свіжі овочі, фета, оливки та оливкова олія.", "110 грн", "Salad"),
        MenuItem("drink1", "Кока-Кола", "Освіжаючий газований напій.", "30 грн", "Drink"),
        MenuItem("drink2", "Сік Апельсиновий", "Натуральний апельсиновий сік.", "40 грн", "Drink")
    )

    override suspend fun getMenuItems(category: String?): DataResult<List<MenuItem>> {
        delay(700) // Simulate network delay
        return try {
            val items = if (category == null) {
                allMenuItems
            } else {
                allMenuItems.filter { it.category.equals(category, ignoreCase = true) }
            }
            if (items.isEmpty() && category != null) {
                DataResult.Success(emptyList()) // Return empty list if category exists but no items
                // Or DataResult.Error("No items found for category: $category")
            } else {
                DataResult.Success(items)
            }
        } catch (e: Exception) {
            DataResult.Error("Failed to load menu items: ${e.message}")
        }
    }

    override suspend fun getMenuItemDetails(itemId: String): DataResult<MenuItem?> {
        delay(300)
        return try {
            DataResult.Success(allMenuItems.find { it.id == itemId })
        } catch (e: Exception) {
            DataResult.Error("Failed to load item details: ${e.message}")
        }
    }
}