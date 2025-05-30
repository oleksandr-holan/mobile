package com.example.lab1.ui.feature.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.example.lab1.R
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.ui.navigation.AppDestinations
import kotlinx.coroutines.flow.collectLatest


const val ORDER_SCREEN_NEW_ORDER_BUTTON_TAG = "order_screen_new_order_button"
const val ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG = "order_screen_active_order_display"
const val ORDER_SCREEN_NO_ACTIVE_ORDER_TEXT_TAG = "order_screen_no_active_order_text"
const val ORDER_SCREEN_FAB_TAG = "order_screen_fab"
const val ORDER_SCREEN_LOADING_ORDER_TAG = "order_screen_loading_order"
const val ORDER_SCREEN_LOADING_ITEMS_TAG = "order_screen_loading_items"
const val ORDER_SCREEN_EMPTY_ORDER_PROMPT_TAG = "order_screen_empty_order_prompt"
const val ORDER_SCREEN_ERROR_MESSAGE_TAG = "order_screen_error_message"


@Composable
fun OrderScreen(
    innerNavController: NavHostController, orderViewModel: OrderViewModel = hiltViewModel()
) {
    val uiState by orderViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(orderViewModel.sideEffect, lifecycleOwner) {
        orderViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).collectLatest { effect ->
            when (effect) {
                is OrderScreenSideEffect.NavigateToEditOrderItem -> {
                    innerNavController.navigate(
                        AppDestinations.EDIT_ORDER_ITEM_DETAILS_ROUTE.replace(
                            "{${AppDestinations.ARG_ITEM_ID}}", effect.orderItemId.toString()
                        )
                    )
                }

                OrderScreenSideEffect.NavigateToMenuScreen -> {
                    val activeOrderId = uiState.currentOrder?.orderId
                    if (activeOrderId != null) {
                        innerNavController.navigate(
                            AppDestinations.MENU_SCREEN_WITH_ORDER_ROUTE.replace(
                                "{${AppDestinations.ARG_ACTIVE_ORDER_ID}}", activeOrderId.toString()
                            )
                        )
                    }
                }
            }
        }
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { orderViewModel.onAction(OrderScreenAction.FabClicked) },
            modifier = Modifier.testTag(ORDER_SCREEN_FAB_TAG)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_items_to_order_fab_desc)
            )
        }
    }) { paddingValues ->
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
                        text = stringResource(
                            R.string.order_id_table_text, order.orderId, order.tableNumber
                        ),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag(ORDER_SCREEN_ACTIVE_ORDER_DISPLAY_TAG)
                    )
                } ?: Text(
                    text = stringResource(R.string.no_active_order_text),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag(ORDER_SCREEN_NO_ACTIVE_ORDER_TEXT_TAG)
                )
                Button(
                    onClick = { orderViewModel.onAction(OrderScreenAction.CreateNewOrder) },
                    modifier = Modifier.testTag(ORDER_SCREEN_NEW_ORDER_BUTTON_TAG)
                ) {
                    Text(stringResource(R.string.new_order_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoadingOrder) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ORDER_SCREEN_LOADING_ORDER_TAG),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Text(
                        stringResource(R.string.loading_order_text),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            uiState.errorMessage?.let {
                Text(
                    text = stringResource(
                        R.string.generic_error_text, it
                    ),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .testTag(ORDER_SCREEN_ERROR_MESSAGE_TAG)

                )
            }

            if (uiState.currentOrder != null && uiState.isLoadingItems) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .testTag(ORDER_SCREEN_LOADING_ITEMS_TAG),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Text(
                        stringResource(R.string.loading_items_text),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            } else if (uiState.currentOrder == null && !uiState.isLoadingOrder) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.create_new_order_prompt), fontSize = 18.sp)
                }
            } else if (uiState.currentOrderItems.isEmpty() && uiState.currentOrder != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(ORDER_SCREEN_EMPTY_ORDER_PROMPT_TAG),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.empty_order_prompt), fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        uiState.currentOrderItems,
                        key = { item -> item.orderItemId }) { orderItem ->
                        OrderItemRow(orderItem = orderItem, onClick = {
                            orderViewModel.onAction(OrderScreenAction.OrderItemClicked(orderItem.orderItemId))
                        }, onDelete = {
                            orderViewModel.onAction(
                                OrderScreenAction.OrderItemSwipedToDelete(
                                    orderItem
                                )
                            )
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(
    orderItem: OrderItemEntity, onClick: () -> Unit, onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        if (it == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
            true
        } else {
            false
        }
    })

    SwipeToDismissBox(
        state = dismissState, backgroundContent = {
            val color = when (dismissState.dismissDirection) {
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
                    contentDescription = stringResource(R.string.delete_item_desc),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }, enableDismissFromStartToEnd = false, enableDismissFromEndToStart = true
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
                        stringResource(
                            R.string.order_item_details_text,
                            orderItem.quantity,
                            orderItem.itemPrice
                        ), style = MaterialTheme.typography.bodySmall
                    )
                    orderItem.specialRequests?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            stringResource(R.string.notes_label, it),
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}