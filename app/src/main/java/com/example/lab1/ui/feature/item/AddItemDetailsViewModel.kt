package com.example.lab1.ui.feature.item

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.util.DataResult
import com.example.lab1.data.repository.OrderRepository
import com.example.lab1.ui.navigation.AppDestinations // For ARG_ITEM_ID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class AddItemDetailsScreenState(
    val itemId: String? = null, // The ID of the item selected from the menu
    val itemName: String = "Loading Item...", // Default while fetching
    // You might want other fields from MenuItem here if they are customizable or displayed
    // val itemDescription: String? = null,
    // val itemPrice: String? = null,
    val quantity: Float = 1f,
    val specialRequests: String = "",
    val isUrgent: Boolean = false,
    val isLoadingDetails: Boolean = true, // True initially while fetching item details
    val errorMessage: String? = null
) {
    val roundedQuantity: Int get() = quantity.roundToInt()
}

sealed class AddItemDetailsAction {
    data class QuantityChanged(val newQuantity: Float) : AddItemDetailsAction()
    data class SpecialRequestsChanged(val requests: String) : AddItemDetailsAction()
    data class UrgencyChanged(val urgent: Boolean) : AddItemDetailsAction()
    data object AddToOrderClicked : AddItemDetailsAction()
    // No explicit LoadItemDetails action needed from UI if it happens on init
}

sealed class AddItemDetailsSideEffect {
    data object NavigateBack : AddItemDetailsSideEffect()
}

class AddItemDetailsViewModel(
    private val orderRepository: OrderRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemDetailsScreenState())
    val uiState: StateFlow<AddItemDetailsScreenState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AddItemDetailsSideEffect>()
    val sideEffect: SharedFlow<AddItemDetailsSideEffect> = _sideEffect.asSharedFlow()

    init {
        val itemIdFromNav: String? = savedStateHandle[AppDestinations.ARG_ITEM_ID]
        _uiState.update { it.copy(itemId = itemIdFromNav) }

        if (itemIdFromNav != null) {
            fetchItemDetails(itemIdFromNav)
        } else {
            // Handle case where itemId is unexpectedly null (e.g., navigation error)
            _uiState.update {
                it.copy(
                    isLoadingDetails = false,
                    errorMessage = "Item ID not provided.",
                    itemName = "Error"
                )
            }
        }
    }

    private fun fetchItemDetails(itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetails = true, errorMessage = null) }
            when (val result = orderRepository.getMenuItemDetails(itemId)) {
                is DataResult.Success -> {
                    result.data?.let { menuItem ->
                        _uiState.update {
                            it.copy(
                                itemName = menuItem.name,
                                // Potentially pre-fill other fields from menuItem
                                // itemDescription = menuItem.description,
                                // itemPrice = menuItem.price,
                                isLoadingDetails = false
                            )
                        }
                    } ?: _uiState.update { // Item ID valid, but item not found in repository
                        it.copy(
                            isLoadingDetails = false,
                            errorMessage = "Item details not found.",
                            itemName = "Unknown Item"
                        )
                    }
                }
                is DataResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingDetails = false,
                            errorMessage = result.message,
                            itemName = "Error"
                        )
                    }
                }
                is DataResult.Loading -> { /* isLoadingDetails is already true */ }
            }
        }
    }

    fun onAction(action: AddItemDetailsAction) {
        when (action) {
            is AddItemDetailsAction.QuantityChanged -> _uiState.update { it.copy(quantity = action.newQuantity) }
            is AddItemDetailsAction.SpecialRequestsChanged -> _uiState.update { it.copy(specialRequests = action.requests) }
            is AddItemDetailsAction.UrgencyChanged -> _uiState.update { it.copy(isUrgent = action.urgent) }
            AddItemDetailsAction.AddToOrderClicked -> {
                if (uiState.value.isLoadingDetails || uiState.value.itemId == null) {
                    // Prevent adding if details are still loading or item is invalid
                    Log.w("AddItemDetailsVM", "Attempted to add to order while loading or item invalid.")
                    return
                }
                handleAddToOrder()
            }
        }
    }

    private fun handleAddToOrder() {
        val currentState = _uiState.value
        Log.d(
            "AddItemDetailsVM",
            "Adding to order: ItemId='${currentState.itemId}', Name='${currentState.itemName}', " +
                    "Qty=${currentState.roundedQuantity}, Urgent=${currentState.isUrgent}, Notes='${currentState.specialRequests}'"
        )
        viewModelScope.launch {
            _sideEffect.emit(AddItemDetailsSideEffect.NavigateBack)
        }
    }
}