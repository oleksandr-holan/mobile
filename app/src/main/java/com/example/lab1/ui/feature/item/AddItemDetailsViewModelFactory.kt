package com.example.lab1.ui.feature.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle // For CreationExtras.createSavedStateHandle()
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lab1.data.repository.OrderRepository

class AddItemDetailsViewModelFactory(
    private val orderRepository: OrderRepository
    // No longer need to pass owner or defaultArgs.
    // SavedStateRegistryOwner is implicitly available via CreationExtras in newer Compose ViewModel retrieval.
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras // extras parameter is new
    ): T {
        if (modelClass.isAssignableFrom(AddItemDetailsViewModel::class.java)) {
            // Get SavedStateHandle from CreationExtras
            val savedStateHandle = extras.createSavedStateHandle()
            return AddItemDetailsViewModel(orderRepository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}