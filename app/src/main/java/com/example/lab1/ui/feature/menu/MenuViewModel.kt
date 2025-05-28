package com.example.lab1.ui.feature.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.model.MenuItem 
import com.example.lab1.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MenuScreenState(
    val menuItems: List<MenuItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedCategory: String? = null 
)

sealed class MenuScreenAction {
    data class MenuItemClicked(val menuItemId: String) : MenuScreenAction()
    data class FilterByCategory(val category: String?) : MenuScreenAction() 
}

sealed class MenuScreenSideEffect {
    data class NavigateToNewOrderItemDetails(val menuItemId: String) : MenuScreenSideEffect()
}

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuScreenState())
    val uiState: StateFlow<MenuScreenState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MenuScreenSideEffect>()
    val sideEffect: SharedFlow<MenuScreenSideEffect> = _sideEffect.asSharedFlow()

    init {
        fetchMenuItems()
    }

    private fun fetchMenuItems(category: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, selectedCategory = category) }
            orderRepository.getAllMenuItems()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "error_fetching_menu_error"
                        )
                    }
                }
                .collectLatest { items ->
                    val filteredItems = if (category == null) {
                        items
                    } else {
                        items.filter { it.category.equals(category, ignoreCase = true) }
                    }
                    _uiState.update {
                        it.copy(
                            menuItems = filteredItems,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onAction(action: MenuScreenAction) {
        when (action) {
            is MenuScreenAction.MenuItemClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(MenuScreenSideEffect.NavigateToNewOrderItemDetails(action.menuItemId))
                }
            }
            is MenuScreenAction.FilterByCategory -> {
                fetchMenuItems(action.category)
            }
        }
    }
}