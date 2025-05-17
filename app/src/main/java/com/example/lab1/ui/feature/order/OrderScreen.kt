package com.example.lab1.ui.feature.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
    import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab1.data.repository.MockOrderRepository
import com.example.lab1.ui.components.MenuItemCard

@Composable
fun OrderScreen(
    onNavigateToAddItem: (itemId: String) -> Unit, // Keep this lambda for navigation triggered by MainAppScreen's NavController
    orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(MockOrderRepository())
    )
) {
    val uiState by orderViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Handle side effects from ViewModel, like navigation requests initiated by ViewModel logic
    LaunchedEffect(orderViewModel.sideEffect, lifecycleOwner) {
        orderViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collect { effect ->
            when (effect) {
                is OrderScreenSideEffect.NavigateToItemDetails -> {
                    onNavigateToAddItem(effect.itemId)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding() // Already present
            .padding(16.dp)
    ) {
        Text(
            text = "Нове замовлення: Стіл №${uiState.tableNumber}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Optional: Category Filter Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { orderViewModel.onAction(OrderScreenAction.FilterByCategory(null)) }) { Text("All") }
            Button(onClick = { orderViewModel.onAction(OrderScreenAction.FilterByCategory("Pizza")) }) { Text("Піца") }
            Button(onClick = { orderViewModel.onAction(OrderScreenAction.FilterByCategory("Salad")) }) { Text("Салати") }
            Button(onClick = { orderViewModel.onAction(OrderScreenAction.FilterByCategory("Drink")) }) { Text("Напої") }
        }


        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (uiState.menuItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No menu items available for this category.", modifier = Modifier.padding(16.dp))
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.menuItems, key = { item -> item.id }) { menuItem ->
                    Box(modifier = Modifier.clickable {
                        // Dispatch an action to the ViewModel when an item is clicked
                        orderViewModel.onAction(OrderScreenAction.MenuItemClicked(menuItem.id))
                    }) {
                        MenuItemCard(
                            itemName = menuItem.name,
                            itemDescription = menuItem.description,
                            price = menuItem.price
                        )
                    }
                }
            }
        }
    }
}