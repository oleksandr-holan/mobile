package com.example.lab1.ui.feature.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle 
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lab1.data.repository.OrderRepository

class AddItemDetailsViewModelFactory(
    private val orderRepository: OrderRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras 
    ): T {
        if (modelClass.isAssignableFrom(AddItemDetailsViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            return AddItemDetailsViewModel(orderRepository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}