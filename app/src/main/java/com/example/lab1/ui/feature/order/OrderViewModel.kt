package com.example.lab1.ui.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.util.DataResult
import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderScreenState(
    val tableNumber: Int = 5, 
    val menuItems: List<MenuItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String? = null 
)

sealed class OrderScreenAction {
    data object LoadMenuItems : OrderScreenAction() 
    data class MenuItemClicked(val itemId: String) : OrderScreenAction()
    data class FilterByCategory(val category: String?) : OrderScreenAction()
}

sealed class OrderScreenSideEffect {
    data class NavigateToItemDetails(val itemId: String) : OrderScreenSideEffect()
}

class OrderViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderScreenState())
    val uiState: StateFlow<OrderScreenState> = _uiState.asStateFlow()

    private val _sideEffect = kotlinx.coroutines.flow.MutableSharedFlow<OrderScreenSideEffect>()
    val sideEffect: kotlinx.coroutines.flow.SharedFlow<OrderScreenSideEffect> = _sideEffect.asSharedFlow()

    init {
        fetchMenuItems()
    }

    fun onAction(action: OrderScreenAction) {
        when (action) {
            OrderScreenAction.LoadMenuItems -> fetchMenuItems()
            is OrderScreenAction.MenuItemClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(OrderScreenSideEffect.NavigateToItemDetails(action.itemId))
                }
            }
            is OrderScreenAction.FilterByCategory -> {
                _uiState.update { it.copy(selectedCategory = action.category, isLoading = true, errorMessage = null) }
                fetchMenuItems(action.category)
            }
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
                            menuItems = emptyList() 
                        )
                    }
                }
                is DataResult.Loading -> { 
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}