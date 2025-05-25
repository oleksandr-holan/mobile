package com.example.lab1.ui.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderScreenState(
    val currentOrder: OrderEntity? = null,
    val currentOrderItems: List<OrderItemEntity> = emptyList(),
    val isLoadingOrder: Boolean = true,
    val isLoadingItems: Boolean = false, // To show loading when items for a new order are fetched
    val errorMessage: String? = null,
    val showMenuScreen: Boolean = false // To trigger navigation to MenuScreen
)

sealed class OrderScreenAction {
    data object CreateNewOrder : OrderScreenAction()
    data class OrderItemClicked(val orderItemId: Long) : OrderScreenAction()
    data class OrderItemSwipedToDelete(val orderItemEntity: OrderItemEntity) : OrderScreenAction()
    data object FabClicked : OrderScreenAction() // To navigate to MenuScreen
    data object MenuScreenDismissed : OrderScreenAction() // To reset showMenuScreen
    data object LoadActiveOrder : OrderScreenAction() // Initial load
}

sealed class OrderScreenSideEffect {
    data class NavigateToEditOrderItem(val orderItemId: Long) : OrderScreenSideEffect()
    data object NavigateToMenuScreen : OrderScreenSideEffect()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderScreenState())
    val uiState: StateFlow<OrderScreenState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<OrderScreenSideEffect>()
    val sideEffect: SharedFlow<OrderScreenSideEffect> = _sideEffect.asSharedFlow()

    // Holds the ID of the currently active order. Can be null if no active order.
    private val activeOrderIdFlow = MutableStateFlow<Long?>(null)

    init {
        loadLatestActiveOrder()

        // Observe the activeOrderIdFlow and fetch its items whenever it changes
        viewModelScope.launch {
            activeOrderIdFlow.filterNotNull().flatMapLatest { orderId ->
                _uiState.update { it.copy(isLoadingItems = true) }
                orderRepository.getOrderItemsForOrder(orderId)
            }.collectLatest { items ->
                _uiState.update {
                    it.copy(
                        currentOrderItems = items,
                        isLoadingItems = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun loadLatestActiveOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingOrder = true) }
            orderRepository.getLatestActiveOrder().collectLatest { order ->
                _uiState.update { it.copy(currentOrder = order, isLoadingOrder = false) }
                activeOrderIdFlow.value = order?.orderId // Update active order ID
                if (order == null) { // No active order, clear items
                    _uiState.update { it.copy(currentOrderItems = emptyList(), isLoadingItems = false) }
                }
            }
        }
    }


    fun onAction(action: OrderScreenAction) {
        when (action) {
            OrderScreenAction.LoadActiveOrder -> {
                loadLatestActiveOrder() // Re-trigger loading if needed
            }
            OrderScreenAction.CreateNewOrder -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoadingOrder = true) }
                    // For simplicity, using a default table number. This could be an input.
                    val newOrderId = orderRepository.createNewOrder(tableNumber = _uiState.value.currentOrder?.tableNumber?.plus(1) ?: 1)
                    activeOrderIdFlow.value = newOrderId // Set the new order as active
                    // The flow for fetching the order itself will update _uiState.currentOrder
                    // Need to explicitly reload the order details for the new ID
                    orderRepository.getOrderById(newOrderId).collectLatest { newOrder ->
                        _uiState.update { it.copy(currentOrder = newOrder, isLoadingOrder = false, currentOrderItems = emptyList()) }
                    }
                }
            }
            is OrderScreenAction.OrderItemClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(OrderScreenSideEffect.NavigateToEditOrderItem(action.orderItemId))
                }
            }
            is OrderScreenAction.OrderItemSwipedToDelete -> {
                viewModelScope.launch {
                    orderRepository.deleteOrderItem(action.orderItemEntity)
                    // The list will auto-update due to the Flow from getOrderItemsForOrder
                }
            }
            OrderScreenAction.FabClicked -> {
                if (_uiState.value.currentOrder == null) {
                    _uiState.update { it.copy(errorMessage = "Please create or select an order first.") }
                    // Optionally, could auto-create an order here.
                } else {
                    viewModelScope.launch { _sideEffect.emit(OrderScreenSideEffect.NavigateToMenuScreen) }
                }
            }
            OrderScreenAction.MenuScreenDismissed -> {
                _uiState.update { it.copy(showMenuScreen = false) }
            }
        }
    }
}