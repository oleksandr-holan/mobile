package com.example.lab1.ui.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.repository.DataResult
import com.example.lab1.data.repository.MenuItem
import com.example.lab1.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- State ---
data class OrderScreenState(
    val tableNumber: Int = 5, // Example, could be dynamic
    val menuItems: List<MenuItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String? = null // To filter items
)

// --- Actions ---
sealed class OrderScreenAction {
    data object LoadMenuItems : OrderScreenAction() // Initial load or refresh
    data class MenuItemClicked(val itemId: String, val itemName: String) : OrderScreenAction()
    data class FilterByCategory(val category: String?) : OrderScreenAction()
    // Add more actions as needed, e.g., AddToCart, ViewCart
}

// --- Side Effects ---
sealed class OrderScreenSideEffect {
    data class NavigateToItemDetails(val itemId: String, val itemName: String) : OrderScreenSideEffect()
    // data class ShowItemAddedMessage(val itemName: String) : OrderScreenSideEffect()
}


class OrderViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderScreenState())
    val uiState: StateFlow<OrderScreenState> = _uiState.asStateFlow()

    // Using MutableSharedFlow for one-time events like navigation
    // For simplicity, if navigation is the only side effect from this screen handled by OrderScreen itself,
    // we might not need a SharedFlow here if the navigation is passed up as a lambda.
    // But if the ViewModel triggers other one-time UI events (like Toasts), it's useful.
    // For this lab, onNavigateToAddItem is passed as a lambda, so we might not need this SharedFlow for THAT specific navigation.
    // However, keeping it for consistency with other ViewModels if they use it.
    // Let's assume for now the navigation to add item details is more complex or might originate from other logic.
    private val _sideEffect = kotlinx.coroutines.flow.MutableSharedFlow<OrderScreenSideEffect>()
    val sideEffect: kotlinx.coroutines.flow.SharedFlow<OrderScreenSideEffect> = _sideEffect.asSharedFlow()


    init {
        // Load menu items when ViewModel is created
        fetchMenuItems()
    }

    fun onAction(action: OrderScreenAction) {
        when (action) {
            OrderScreenAction.LoadMenuItems -> fetchMenuItems()
            is OrderScreenAction.MenuItemClicked -> {
                viewModelScope.launch {
                    // The lab asks for AddItemDetailsScreen, so we navigate there.
                    // The item name is passed for display on the details screen.
                    _sideEffect.emit(OrderScreenSideEffect.NavigateToItemDetails(action.itemId, action.itemName))
                }
            }
            is OrderScreenAction.FilterByCategory -> {
                _uiState.update { it.copy(selectedCategory = action.category, isLoading = true, errorMessage = null) }
                fetchMenuItems(action.category)
            }

            else -> {}
        }
    }

    private fun fetchMenuItems(category: String? = _uiState.value.selectedCategory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = orderRepository.getMenuItems(category)) {
                is DataResult.Success -> {
                    _uiState.update {
                        it.copy(
                            menuItems = result.data,
                            isLoading = false
                        )
                    }
                }
                is DataResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.message,
                            isLoading = false,
                            menuItems = emptyList() // Clear items on error
                        )
                    }
                }
                is DataResult.Loading -> { // Handle if repository emits Loading
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}