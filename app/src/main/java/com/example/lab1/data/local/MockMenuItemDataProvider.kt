package com.example.lab1.data.local

import com.example.lab1.data.model.MenuItem

object MockMenuItemDataProvider {
    fun getMockMenuItems(): List<MenuItem> {
        return listOf(
            MenuItem("pizza1", "Піца Маргарита", "Класична піца з томатним соусом, моцарелою та базиліком.", "150.00", "Pizza", imageUrl = null),
            MenuItem("pizza2", "Піца Пепероні", "Гостра піца з салямі пепероні та моцарелою.", "180.00", "Pizza", imageUrl = null),
            MenuItem("pizza3", "Піца Гавайська", "Піца з шинкою, ананасами та моцарелою.", "170.00", "Pizza", imageUrl = null),
            MenuItem("salad1", "Салат Цезар", "Салат з куркою, грінками, пармезаном та соусом Цезар.", "120.00", "Salad", imageUrl = null),
            MenuItem("salad2", "Грецький Салат", "Свіжі овочі, фета, оливки та оливкова олія.", "110.00", "Salad", imageUrl = null),
            MenuItem("drink1", "Кока-Кола", "Освіжаючий газований напій.", "30.00", "Drink", imageUrl = null),
            MenuItem("drink2", "Сік Апельсиновий", "Натуральний апельсиновий сік.", "40.00", "Drink", imageUrl = null),
            MenuItem("dessert1", "Тірамісу", "Класичний італійський десерт.", "90.00", "Dessert", imageUrl = null)
        )
    }
}