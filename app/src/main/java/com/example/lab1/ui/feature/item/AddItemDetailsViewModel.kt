package com.example.lab1.ui.feature.item

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.data.repository.OrderRepository
import com.example.lab1.ui.navigation.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class AddItemDetailsScreenState(
    val currentOrderItemId: Long? = null, // Null if creating new
    val activeOrderIdForNewItem: Long? = null, // Only for new items
    val menuItemOriginalId: String? = null, // ID of the base MenuItem
    val itemName: String = "Loading...",
    var itemPrice: String = "0.00", // Price at the time of adding to order
    val quantity: Float = 1f,
    val specialRequests: String = "",
    val isUrgent: Boolean = false, // Kept for now
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val buttonText: String = "Add to Order"
) {
    val roundedQuantity: Int get() = quantity.roundToInt()
    val isEditMode: Boolean get() = currentOrderItemId != null
}

sealed class AddItemDetailsAction {
    data class QuantityChanged(val newQuantity: Float) : AddItemDetailsAction()
    data class SpecialRequestsChanged(val requests: String) : AddItemDetailsAction()
    data class UrgencyChanged(val urgent: Boolean) : AddItemDetailsAction()
    data object SaveOrUpdateItemClicked : AddItemDetailsAction()
}

sealed class AddItemDetailsSideEffect {
    data object NavigateBack : AddItemDetailsSideEffect()
}

@HiltViewModel
class AddItemDetailsViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemDetailsScreenState())
    val uiState: StateFlow<AddItemDetailsScreenState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AddItemDetailsSideEffect>()
    val sideEffect: SharedFlow<AddItemDetailsSideEffect> = _sideEffect.asSharedFlow()

    init {
        val editingOrderItemId: Long? = savedStateHandle[AppDestinations.ARG_ITEM_ID]
        val newFromMenuItemId: String? = savedStateHandle[AppDestinations.ARG_MENU_ITEM_ID]
        val activeOrderIdForNew: Long? = savedStateHandle[AppDestinations.ARG_ACTIVE_ORDER_ID]

        _uiState.update { it.copy(buttonText = if (editingOrderItemId != null) "Update Item" else "Add to Order") }

        if (editingOrderItemId != null && editingOrderItemId != 0L) { // Editing existing OrderItem
            _uiState.update { it.copy(currentOrderItemId = editingOrderItemId) }
            fetchOrderItemDetailsForEdit(editingOrderItemId)
        } else if (newFromMenuItemId != null && activeOrderIdForNew != null && activeOrderIdForNew != 0L) { // Creating new from MenuItem
            _uiState.update { it.copy(activeOrderIdForNewItem = activeOrderIdForNew) }
            fetchMenuItemDetailsForNewOrder(newFromMenuItemId)
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Invalid item or order information provided.",
                    itemName = "Error"
                )
            }
        }
    }

    private fun fetchMenuItemDetailsForNewOrder(menuItemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            orderRepository.getMenuItemById(menuItemId).collectLatest { menuItem ->
                if (menuItem != null) {
                    _uiState.update {
                        it.copy(
                            menuItemOriginalId = menuItem.id,
                            itemName = menuItem.name,
                            itemPrice = menuItem.price, // Price from MenuItem is the initial price
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Menu item not found.",
                            itemName = "Unknown Item"
                        )
                    }
                }
            }
        }
    }

    private fun fetchOrderItemDetailsForEdit(orderItemId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            orderRepository.getOrderItemById(orderItemId).collectLatest { orderItemEntity ->
                if (orderItemEntity != null) {
                    _uiState.update {
                        it.copy(
                            menuItemOriginalId = orderItemEntity.menuOriginalId,
                            itemName = orderItemEntity.itemName,
                            itemPrice = orderItemEntity.itemPrice,
                            quantity = orderItemEntity.quantity.toFloat(),
                            specialRequests = orderItemEntity.specialRequests ?: "",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Order item details not found.",
                            itemName = "Unknown Item"
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: AddItemDetailsAction) {
        _uiState.update { it.copy(errorMessage = null) } // Clear previous error on new action

        when (action) {
            is AddItemDetailsAction.QuantityChanged -> _uiState.update { it.copy(quantity = action.newQuantity) }
            is AddItemDetailsAction.SpecialRequestsChanged -> _uiState.update { it.copy(specialRequests = action.requests) }
            is AddItemDetailsAction.UrgencyChanged -> _uiState.update { it.copy(isUrgent = action.urgent) } // isUrgent not saved yet
            AddItemDetailsAction.SaveOrUpdateItemClicked -> handleSaveOrUpdate()
        }
    }

    private fun handleSaveOrUpdate() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isSaving) return
        if (currentState.menuItemOriginalId == null) {
            _uiState.update { it.copy(errorMessage = "Cannot save, item base information missing.")}
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                if (currentState.isEditMode && currentState.currentOrderItemId != null) {
                    val existingItem = orderRepository.getOrderItemById(currentState.currentOrderItemId).first() // Get current value
                    if (existingItem != null) {
                        val itemToUpdate = existingItem.copy(
                            quantity = currentState.roundedQuantity,
                            specialRequests = currentState.specialRequests.takeIf { it.isNotBlank() }
                            // itemName and itemPrice are fixed once added. isUrgent not saved.
                        )
                        orderRepository.updateOrderItem(itemToUpdate)
                        _sideEffect.emit(AddItemDetailsSideEffect.NavigateBack)
                    } else {
                        _uiState.update { it.copy(errorMessage = "Failed to find item to update.") }
                    }

                } else if (currentState.activeOrderIdForNewItem != null) { // Create new
                    val newOrderItem = OrderItemEntity(
                        orderIdFk = currentState.activeOrderIdForNewItem,
                        menuOriginalId = currentState.menuItemOriginalId,
                        itemName = currentState.itemName,
                        itemPrice = currentState.itemPrice,
                        quantity = currentState.roundedQuantity,
                        specialRequests = currentState.specialRequests.takeIf { it.isNotBlank() }
                    )
                    orderRepository.addOrderItemToOrder(newOrderItem)
                    _sideEffect.emit(AddItemDetailsSideEffect.NavigateBack)
                } else {
                    _uiState.update { it.copy(errorMessage = "Order information missing.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error saving item: ${e.localizedMessage}") }
                Log.e("AddItemDetailsVM", "Error saving item", e)
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}