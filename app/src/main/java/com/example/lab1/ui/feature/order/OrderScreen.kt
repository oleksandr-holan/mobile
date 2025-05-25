package com.example.lab1.ui.feature.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.lab1.data.model.OrderItemEntity
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.example.lab1.ui.navigation.AppDestinations


@Composable
fun OrderScreen(
    innerNavController: NavHostController, 
    orderViewModel: OrderViewModel = hiltViewModel() 
) {
    val uiState by orderViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(orderViewModel.sideEffect, lifecycleOwner) {
        orderViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collectLatest { effect ->
            when (effect) {
                is OrderScreenSideEffect.NavigateToEditOrderItem -> {
                    innerNavController.navigate(
                        AppDestinations.EDIT_ORDER_ITEM_DETAILS_ROUTE.replace(
                            "{${AppDestinations.ARG_ITEM_ID}}",
                            effect.orderItemId.toString()
                        )
                    )
                }
                OrderScreenSideEffect.NavigateToMenuScreen -> {
                    val activeOrderId = uiState.currentOrder?.orderId 
                    if (activeOrderId != null) {
                        innerNavController.navigate(
                            AppDestinations.MENU_SCREEN_WITH_ORDER_ROUTE.replace(
                                "{${AppDestinations.ARG_ACTIVE_ORDER_ID}}",
                                activeOrderId.toString()
                            )
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { orderViewModel.onAction(OrderScreenAction.FabClicked) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add items to order")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) 
                .safeDrawingPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.currentOrder?.let { order ->
                    Text(
                        text = "Order: #${order.orderId} (Table ${order.tableNumber})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } ?: Text(
                    text = "No Active Order",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = { orderViewModel.onAction(OrderScreenAction.CreateNewOrder) }) {
                    Text("New Order")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoadingOrder) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text("Loading order...", modifier = Modifier.padding(start = 8.dp))
                }
            }

            uiState.errorMessage?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (uiState.currentOrder != null && uiState.isLoadingItems) {
                Box(modifier = Modifier.fillMaxWidth().padding(top=16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text("Loading items...", modifier = Modifier.padding(start = 8.dp))
                }
            } else if (uiState.currentOrder == null && !uiState.isLoadingOrder) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Please create a new order to add items.", fontSize = 18.sp)
                }
            } else if (uiState.currentOrderItems.isEmpty() && uiState.currentOrder != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("This order is empty. Click '+' to add items.", fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.currentOrderItems, key = { item -> item.orderItemId }) { orderItem ->
                        OrderItemRow(
                            orderItem = orderItem,
                            onClick = {
                                orderViewModel.onAction(OrderScreenAction.OrderItemClicked(orderItem.orderItemId))
                            },
                            onDelete = {
                                orderViewModel.onAction(OrderScreenAction.OrderItemSwipedToDelete(orderItem))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(
    orderItem: OrderItemEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) { 
                onDelete()
                true 
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = when(dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(12.dp),
                contentAlignment = Alignment.CenterEnd 
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete item",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        enableDismissFromStartToEnd = false, 
        enableDismissFromEndToStart = true
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(orderItem.itemName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Qty: ${orderItem.quantity} Price: ${orderItem.itemPrice}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    orderItem.specialRequests?.takeIf { it.isNotBlank() }?.let {
                        Text("Notes: $it", style = MaterialTheme.typography.bodySmall, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                }
            }
        }
    }
}